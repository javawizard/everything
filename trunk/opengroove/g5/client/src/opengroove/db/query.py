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
        return "objects.path like ? escape ?", [self.path.replace("/", "//") + "///%", "/"]


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

print "TODO: add support for limit and offset in queries, and add"
print "support for the sort stuff, and allow users to get the"
print "query results either by paths or by actual objects"

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
        """
        self.db = db
        self.filters = []
        self.offset = None
        self.limit = None
        self.sort_order = []
    
    def sort_up(self, attribute):
        """
        Adds a sort ordering to this query. This sort ordering will sort on the
        specified attribute. The sort will be ascending.
        """
        self.sort_order.append((attribute, "asc"))
        return self
    
    def sort_down(self, attribute):
        """
        Adds a sort ordering to this query. This sort ordering will sort on the
        specified attribute. The sort will be descending.
        """
        self.sort_order.append((attribute, "desc"))
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
        return PrefixFilter("not", SQLFilter(*self.get_filter_sql()))
    
    invert = inverse
    
    def get_filter_sql(self):
        statements = []
        params = []
        for filter in self.filters:
            filter_statement, filter_params = filter.sql()
            statements.append("(" + filter_statement + ")")
            params += list(filter_params)
        return " and ".join(statements), params
    
    def __and__(self, other):
        """
        Returns a new query that selects objects only if they would be selected
        by both of the filters on either side of the & operator.
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
        either of the filters on either side of the | operator.
        """
        if not isinstance(other, Query):
            return NotImplemented
        query = Query(self.db)
        first = SQLFilter(*self.get_filter_sql())
        second = SQLFilter(*other.get_filter_sql())
        query.filters.append(BinaryFilter(first, second, " or "))
        return query
    
    

































