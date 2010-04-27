import core
from operator import add as operator_add


class BinaryFilter(object):
    """
    A filter that merges two other filters with a particular binary operation.
    For example, the binary operation "and" would cause this filter to allow
    only objects that satisfy the conditions in both of the supplied filters,
    whereas the binary operation "or" would cause this filter to allow objects
    that satisfy either of the supplied filters.
    """
    def __init__(self, first, second, modifier):
        self.first = first
        self.second = second
        self.modifier = modifier
    def sql(self):
        first_sql, first_params = self.first.sql()
        second_sql, second_params = self.second.sql()
        return ("(" + first_sql + ") " + self.modifier + " (" + second_sql + ")",
                first_params + second_params)


class PrefixFilter(object):
    """
    A filter that applies a particular unary prefix to another filter. This
    is most commonly used with the "not" prefix, which allows objects through
    this filter that do not match the supplied filter.
    """
    def __init__(self, modifier, filter):
        self.modifier = modifier
        self.filter = filter
    
    def sql(self):
        sql, params = self.filter.sql()
        return self.modifier + " (" + sql + ")", params


class LiteralAttributeFilter(object):
    """
    A filter that allows objects through if the specified attribute satisfies the
    specified operation on the specified literal value. For example, passing "example"
    as the attribute, "=" as the operation, and 5 as the value would return all
    objects that have an attribute named "example" equal to the integer 5. Any
    valid SQL operation can be used, including operations specified as words (such
    as "LIKE").
    """
    def __init__(self, attribute, operation, value):
        self.attribute = attribute
        self.operation = operation
        self.value = value
    
    def sql(self):
        return ("(select value from attributes where attributes.path = objects.path "
                + "and attributes.name = ?) " + self.operation + " ?",
                [self.attribute, self.value])


class AncestorFilter(object):
    """
    A filter that allows objects through if they have a certain object, specified
    by path, as an ancestor.
    """
    def __init__(self, ancestor_path):
        self.path = ancestor_path
    
    def sql(self):
        return "objects.path like ? escape ?", [self.path.replace("/", "//")
                                                .replace("%", "/%")
                                                .replace("_", "/_") + "//%", "/"]


class ParentFilter(object):
    """
    A filter that allows objects through if they have a certain object, specified
    by path, as their immediate parent.
    """
    def __init__(self, parent_path):
        self.path = parent_path
    
    def sql(self):
        return "objects.parent = ?", [self.path]


class SQLFilter(object):
    """
    A filter that provides a raw piece of SQL to filter with. This is used internally
    by the Query class to implement its & and | operators.
    """
    def __init__(self, query, params):
        self.query = query
        self.params = params
    
    def sql(self):
        return self.query, self.params


class Query(object):
    """
    A query that can be executed against the database.
    
    The documentation refers to both "letting an object pass" and "selecting
    an object". Both of these mean that the object will be returned from the
    query.
    
    Most methods on this class return the query instance they were called on.
    This allows for method invocation chaining.
    """
    def __init__(self, db):
        """
        Creates a new, blank query. Running such a blank query without further
        constraining it would select all of the objects in the database, which
        is generally costly.
        
        Generally, you won't actually use this. What you'll usually do is call
        the query() method on the database or the db_query() or db_query_all()
        methods on objects obtained from the database.
        """
        self.db = db
        self.filters = []
        self.offset_value = None
        self.limit_value = None
        self.sort_order = []
    
    def sort_up(self, attribute):
        """
        Adds a sort ordering to this query. This sort ordering will sort on the
        specified attribute. The sort will be ascending.
        """
        self.sort_order.append(attribute + " asc")
        return self
    
    def sort_down(self, attribute):
        """
        Adds a sort ordering to this query. This sort ordering will sort on the
        specified attribute. The sort will be descending.
        """
        self.sort_order.append(attribute + " desc")
        return self
    
    def ancestor(self, ancestor_path):
        """
        Adds an ancestor filter to this query. Only objects that have the specified
        object as an ancestor will be selected by this query.
        """
        self.filters.append(AncestorFilter(ancestor_path))
        return self
    
    def parent(self, parent_path):
        """
        Adds a parent filter to this query. Only objects that have the specified
        object as a parent will be selected by this query.
        """
        self.filters.append(ParentFilter(parent_path))
        return self
    
    def attribute_equals(self, attribute, value):
        """
        Adds a filter to this query that lets objects pass only if they contain the
        specified attribute and its value is equal to the specified literal value.
        """
        self.filters.append(LiteralAttributeFilter(attribute, "=", value))
        return self
    
    def attribute_less_than(self, attribute, value):
        """
        Adds a filter to this query that lets objects pass only if they contain the
        specified attribute and its value is less than the specified literal value.
        """
        self.filters.append(LiteralAttributeFilter(attribute, "<", value))
        return self

    def attribute_greater_than(self, attribute, value):
        """
        Adds a filter to this query that lets objects pass only if they contain the
        specified attribute and its value is less than the specified literal value.
        """
        self.filters.append(LiteralAttributeFilter(attribute, ">", value))
        return self
    
    def inverse(self):
        """
        Returns a new query that selects objects only if they would not be selected
        by this query.
        
        If you only want to invert a small part of the overall query, you can do that
        by constructing everything else in your query, creating a separate query,
        filtering it by the part you want to invert, inverting it, and then using the
        & operator to get a new query that only inverts the one query.
        """
        query = Query(self.db)
        query.filters.append(PrefixFilter("not", SQLFilter(*self.get_filter_sql())))
        return query
    
    def offset(self, offset_value):
        """
        Sets the offset of this query to be the specified value. The query will skip
        the specified number of matching objects at the beginning of the results when
        running this query. For example, if this is 3, the first 3 objects that would
        be selected by the query will be skipped.
        
        If you don't want an offset on the query, you can pass None to this method.
        If this method is never called on a query, the offset defaults to None.
        """
        self.offset_value = offset_value
        return self
    
    def limit(self, limit_value):
        """
        Sets the limit of this query to be the specified value. The query will only
        return this many objects. If more objects would be selected by the query,
        the remaining objects will be ignored.
        
        This can be used along with offset() to implement a sort of paging on
        queries, to obtain blocks of objects in a particular order.
        
        If you don't want a limit on the query, you can pass None to this method.
        If this method is never called on a query, the limit defaults to None.
        """
        self.limit_value = limit_value
        return self
    
    invert = inverse
    
    def get_filter_sql(self):
        """
        Gets the SQL that should be used for the filter part of the query. This
        is the part that should be added to the query after the "where" clause.
        If this query has no filters, None will be returned.
        """
        if not self.filters:
            return None
        statements = []
        params = []
        for filter in self.filters:
            filter_statement, filter_params = filter.sql()
            statements.append("(" + filter_statement + ")")
            params += list(filter_params)
        return " and ".join(statements), params
    
    def get_sql(self):
        statement = "select path, id, parent from objects"
        params = []
        if self.filters:
            cs, new_params = self.get_filter_sql()
            statement += " where " + cs
            params += new_params
        if self.sort_order:
            # This is vulnerable to SQL injection by injecting the name of a
            # field to set by. I'm not particularly concerned with it, however,
            # as, the database being an embedded database, pretty much any
            # application that has access to it would have direct file-system
            # access to the database anyway.
            statement += " order by " + ", ".join(self.sort_order)
        if self.limit_value is not None:
            statement += " limit ?"
            params.append(self.limit_value)
        if self.offset_value is not None:
            statement += " offset ?"
            params.append(self.offset_value)
        return statement, params
    
    def objects(self):
        """
        Executes this query. All of the objects that this query selects
        will be put into a list and returned. The objects' attributes are
        retrieved during this method call, so future modifications to the
        database after this call will not affect the attributes present
        on the objects in the returned list.
        
        This method tends to be somewhat inefficient, as it executes one
        query for each selected object to get the object's attributes. If
        the objects do not need to all be retrieved from the database, the
        paths() method should be used, as it only executes one query total,
        regardless of how many objects would be returned.
        """
        with self.db.lock:
            results = self.db.sqldb.execute(*self.get_sql())
            return [core.DataObject(self.db, id, path, parent) for
                    path, id, parent in results]
    
    def paths(self):
        """
        Executes this query. The paths of all of the objects that this
        query selects will be put into a list and returned.
        
        This method tends to be more efficient than objects() as this
        method only results in one single query, whereas objects()
        results in one query for each selected object to get the
        object's attributes.
        """
        with self.db.lock:
            results = self.db.sqldb.execute(*self.get_sql())
            return [path for path, id, parent in results]
    
    def __and__(self, other):
        """
        Returns a new query that selects objects only if they would be selected
        by both of the queries on either side of the & operator.
        """
        if not isinstance(other, Query):
            return NotImplemented
        query = Query(self.db)
        first = SQLFilter(*self.get_filter_sql())
        second = SQLFilter(*other.get_filter_sql())
        query.filters.append(BinaryFilter(first, second, " and "))
        return query
    
    def __or__(self, other):
        """
        Returns a new query that selects objects if they would be selected by
        either of the queries on either side of the | operator.
        """
        if not isinstance(other, Query):
            return NotImplemented
        query = Query(self.db)
        first = SQLFilter(*self.get_filter_sql())
        second = SQLFilter(*other.get_filter_sql())
        query.filters.append(BinaryFilter(first, second, " or "))
        return query
    
    

































