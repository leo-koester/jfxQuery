package jfxQuery;


// imports
import java.util.function.BiFunction;


// interface
public interface jfxQueryTraversing {


    //
    // each - Function<Object,Integer,Boolean>
    static jfxQuery each( BiFunction<Object,Integer,Boolean> biFunction, jfxQuery jfxQObj ){

        int idx = 0;
        for ( Object obj : jfxQObj.getCurrentMatchingSelection() )
            if ( !biFunction.apply( obj, idx++ ) ) break;

        return jfxQObj;
    }





}
