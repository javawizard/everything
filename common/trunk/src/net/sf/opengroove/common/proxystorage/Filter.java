package net.sf.opengroove.common.proxystorage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A proxystorage method can be annotated with this, which will result in either
 * it's parameters or it's result being filtered by the filter specified. For
 * examle, a {@link Search} method could be parameter-filtered to alter the
 * search parameters in some way, or a setter could be parameter-filtered to
 * throw an exception if an object not meeting certain criteria is set.<br/><br/>
 * 
 * At least one of {@link #parameterFilter()} or {@link #resultFilter()} should
 * be specified. It is not an error to leave unspecified both of those, but the
 * proxy storage system ends up acting as if this annotation were not present at
 * all. Both parameters can be specified, if desired.
 * 
 * @author Alexander Boyd
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Filter
{
    /**
     * The parameter filter to use.
     * 
     * @return
     */
    public Class<ParameterFilter> parameterFilter();
    
    /**
     * The result filter to use.
     * 
     * @return
     */
    public Class<ResultFilter> resultFilter();
}
