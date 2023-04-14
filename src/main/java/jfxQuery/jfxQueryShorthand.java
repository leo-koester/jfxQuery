package jfxQuery;


public interface jfxQueryShorthand {


    default jfxQuery $( String selectors ){ return new jfxQuery(selectors); }


    default jfxQuery $( String selectors, Object parentElement ){
        return new jfxQuery(selectors, parentElement );
    }


    default jfxQuery $( Object node ){ return new jfxQuery(node); }


    default <T> jfxQuery $( T[] array ){ return new jfxQuery( array ); }

}
