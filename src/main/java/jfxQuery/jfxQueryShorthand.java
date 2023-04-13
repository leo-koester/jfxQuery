package jfxQuery;

import javafx.scene.Node;

public interface jfxQueryShorthand {


    default jfxQuery $( String selectors ){ return new jfxQuery(selectors); }


    default jfxQuery $( Object node ){ return new jfxQuery(node); }


    default <T> jfxQuery $( T[] array ){ return new jfxQuery( array ); }

}
