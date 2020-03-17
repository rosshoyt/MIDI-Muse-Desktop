package com.rosshoyt.parallelmidi.gui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.function.Function;

/**
 * Utilities for populating columns of TableView based on different array index values
 */
public class TableViewUtils {

   // https://stackoverflow.com/questions/52244810/how-to-fill-tableviews-column-with-a-values-from-an-array
   public static <S, T> Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>> createArrayValueFactory(Function<S, T[]> arrayExtractor, final int index) {
      if (index < 0) {
         return cd -> null;
      }
      return cd -> {
         T[] array = arrayExtractor.apply(cd.getValue());
         return array == null || array.length <= index ? null : new SimpleObjectProperty<>(array[index]);
      };
   }
   // https://stackoverflow.com/questions/32206092/java8-method-reference-used-as-function-object-to-combine-functions
   public static <T1, T2> Function<T1, T2> asFunction(Function<T1, T2> function) {
      return function;
   }
}
