package jfxQuery;

// imports
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import java.util.function.BiConsumer;


// class
public interface jfxQueryEventHandlers {

    // on
    static jfxQuery on( String onEvent, BiConsumer<Node,Event> consumer, jfxQuery jfxQObj ){

        jfxQueryTraversing.each(( obj,index ) -> {
            attachEvent( onEvent, obj, consumer );
            return true;
        }, jfxQObj);

        return jfxQObj;
    }


    //
    // private methods
    private static void attachEvent( String onEvent, Object obj, BiConsumer<Node,Event> consumer ){

        switch ( onEvent ){
            case "click" -> {
                if ( obj instanceof Button ref )
                    ref.setOnAction( actionEvent -> consumer.accept( ref, actionEvent ) );

                else if ( obj instanceof Node ref )
                    ref.setOnMouseClicked( mouseEvent -> {
                        if ( mouseEvent.getButton() == MouseButton.PRIMARY )
                            consumer.accept( ref, mouseEvent );
                    });
            }
            case "change" -> {}
            case "keypress" -> {}
            case "keyup" -> {}
            case "mousedown" -> {}
            case "mouseup" -> {}
        }

    }


}
