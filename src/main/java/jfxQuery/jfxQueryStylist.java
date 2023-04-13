package jfxQuery;


// imports
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


// interface
public interface jfxQueryStylist {

    //
    // updateStyle() -
    static void updateStyle( Node node, Map<String,Object> cssAttributes ){

        final String[] currentStyle = { node.getStyle() };

        int[] index = {0};
        String[] jfxStyles = new String[cssAttributes.size()];

        cssAttributes.forEach( ( cssAttribute, value ) -> {

            if ( !(value instanceof String || value instanceof Number) )
                throw new RuntimeException("jfxQueryStylist.updateStyle only accepts String or Number as a value");

            String jfxStyle = getJFXStyle(cssAttribute)+": "+value + ";";
            jfxStyles[ index[0]++ ] = jfxStyle;

            // removes potentially already-applied properties
            currentStyle[0] = currentStyle[0].replaceAll( jfxStyle+":[^;]+(?:;|$)","" );
        });

        node.setStyle( ( currentStyle[0] +" "+ String.join( "; ", jfxStyles ) ).trim() );

        applySpecialProcessing( node, cssAttributes );
    }


    //
    // Some css properties require some special processing to make it work properly
    private static void applySpecialProcessing( Node node, Map<String,Object> cssAttributes ) {

        cssAttributes.forEach( ( cssAttribute, value ) -> {
            switch ( cssAttribute ){
                case "margin" -> {
                    Insets inset = getInsets( value );

                    BorderPane.setMargin( node, inset );
                    HBox.setMargin( node, inset );
                    VBox.setMargin( node, inset );
                    GridPane.setMargin( node, inset );
                }
            }
        });

    }


    //
    // getInsets() - returns an inset based on value to be used in paddings and margins
    private static Insets getInsets( Object value ) {
        double top = 0, right = 0, bottom = 0, left = 0;

        if ( value instanceof Number )
            top = right = bottom = left = ((Number) value ).doubleValue();

        else if ( value instanceof String ){
            String[] values = value.toString().split( "[^0-9.]+" );
            top = right = bottom = left = Double.parseDouble( values[0] );
            if ( values.length >= 2 ) right = left = Double.parseDouble( values[1] );
            if ( values.length >= 3 ) bottom = Double.parseDouble( values[2] );
            if ( values.length >= 4 ) left = Double.parseDouble( values[3] );
        }

        return new Insets( top, right, bottom, left );
    }


    ///
    private static String getJFXStyle( String cssAttribute ) {

        String result = Map.of(
            "color", "text-fill"
        ).get( cssAttribute );

        if ( result == null ) result = cssAttribute;

        if ( !result.startsWith( "-fx-" ) )
            result = "-fx-"+result;

        return result;
    }


    //
    //


}
