/**
 * @program: jfxQuery
 * @author: Leonidio Koester Junior (leo_k)
 * @date: 2023-04-07
 * @description: jfxQuery
 * @version: v1
 */

package jfxQuery;


// imports

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;


// class
final public class jfxQuery
{

    private final List<Object> currentMatchingSelection;


    //
    // class constructors
    public jfxQuery( String selectorString, Parent parentElement ){
        currentMatchingSelection = new ArrayList<>();

        List<jfxQuerySelectors> selectors = jfxQuerySelectors.parse( selectorString );

        if ( parentElement == null ){
            Window window = Stage.getWindows().stream().filter(Window::isShowing).toList().get( 0 );
            parentElement = window.getScene().getRoot();
        }

        findMatch( parentElement, selectors, 0, true );
    }

    public jfxQuery( String selectorString ){
        this(selectorString, null);
    }


    public jfxQuery( Object object ){
        currentMatchingSelection = List.of(object);
    }


    public <T> jfxQuery( T[] array ){
        currentMatchingSelection = List.of(array);
    }


    //
    //
    protected List<?> getCurrentMatchingSelection(){
        return currentMatchingSelection;
    }



    private void findMatch( Node node, List<jfxQuerySelectors> selectors, int selectorIndex, boolean keepTrying ) {

        jfxQuerySelectors selector = selectors.get(selectorIndex);
        Parent parent = null;

        if ( selector.is( node ) ){
            if ( !selector.isContainer() )
                currentMatchingSelection.add( node );

            else if ( node instanceof Parent ref ){
                parent = ref;
                selectorIndex++;
            }
        }
        else if ( node instanceof Parent ref ){
            parent = ref;
            if ( selectorIndex > 0 && selector.hasDescendants() )
                selectorIndex++;
            //keepTrying = false;
        }

        if ( parent != null && selectorIndex < selectors.size() ){
            int finalSelectorIndex = selectorIndex;
            boolean finalKeepTrying = keepTrying;
            parent.getChildrenUnmodifiable().forEach( child ->
                findMatch( child, selectors, finalSelectorIndex, finalKeepTrying )
            );
        }
    }


    /**
     * EACH
     * Traverses the last match by applying a BiFunction where:
     * [Object] represents the matched object;
     * [Integer] represents the index of that object inside the last match
     * [Boolean] - is false, will stop the iteration
     * */
    public jfxQuery each( BiFunction<Object,Integer,Boolean> biFunction ){
        return jfxQueryTraversing.each( biFunction, this );
    }


    /**
     * EACH
     * Traverses the last match by applying a BiConsumer where:
     * [Object] represents the matched object
     * */
    public jfxQuery each( Consumer<Object> consumer ){
        return each( ( object, index ) -> {
            consumer.accept( object );
            return true;
        });
    }


    /**
     * EACH
     * Traverses the last match by applying a BiConsumer where:
     * [Object] represents the matched object;
     * [Integer] represents the index of that object inside the last match
     * */
    public jfxQuery each( BiConsumer<Object,Integer> biConsumer ){
        return each(( object, index ) -> {
            biConsumer.accept( object, index );
            return true;
        });
    }


    /**
     * EACH
     * Traverses the last match by applying a Function where:
     * [Object] represents the matched object;
     * [Boolean] - is false, will stop the iteration
     * */
    public jfxQuery each( Function<Object,Boolean> function ){
        return each(( object, index ) -> {
            return function.apply( object );
        });
    }



    //
    // Manipulators
    /**
     * ATTR - SETTER
     * */
    public jfxQuery attr( String attributeName, Object value ){
        return each( (Consumer<Object>) obj -> jfxQueryCollector.define( obj, attributeName, value ) );
    }


    /**
     * TEXT()
     * */
    // getter
    public String text(){
        if ( currentMatchingSelection.size() > 0 )
            return jfxQueryCollector.collect( currentMatchingSelection.get(0), "text" );
        return null;
    }
    // setter
    public jfxQuery text( String value ){
        return attr( "text", value );
    }


    /**
     * CSS()
     * */
    public jfxQuery css( String cssAttribute, Object value ){
        each( obj -> {
            jfxQueryStylist.updateStyle( ((Node) obj), Map.of(cssAttribute, value) );
        });
        return this;
    }
    public jfxQuery css( Map<String,Object> cssAttributes ){
        each( obj -> {
            jfxQueryStylist.updateStyle( ((Node) obj), cssAttributes );
        });
        return this;
    }



    /**
     * GET()
     * */
    public Node get( int index ){
        return (Node) jfxQueryCollector.get( index, this );
    }


    //
    // Event Handlers
    /**
     *
     * */
    public jfxQuery on( String onEvent, BiConsumer<Node,Event> consumer ){
        return jfxQueryEventHandlers.on( onEvent, consumer, this );
    }



}
