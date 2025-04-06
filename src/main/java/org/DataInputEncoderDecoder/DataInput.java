package org.DataInputEncoderDecoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DataInput {

    private final List<Object> _listOfObjectsInternal;

    // Put all the limits here.
    private static final int MAX_STRING_LENGTH = 1000000;
    private static final int MAX_STRING_ARRAY_LENGTH = 1000;

    public DataInput() {
        _listOfObjectsInternal = new ArrayList<>();
    }
    // Single element constructor.
    public DataInput(Object element) {
        this._listOfObjectsInternal = new ArrayList<>();
        validateAndAddToArray(element);
    }

    // Variable length argument constructor as there can be multiple data types.
    public DataInput(Object... elements) {
        _listOfObjectsInternal = new ArrayList<>();
        // Check type of each object and populate _listOfObjectsInternal.
        for (Object element : elements) {
            validateAndAddToArray(element);
        }
    }

    private void validateAndAddToArray(Object element) {
        switch ((Object) element) {
            case String s when s.length() > MAX_STRING_LENGTH ->
                    throw new IllegalArgumentException("String length exceeds maximum allowed length");
            case String s -> _listOfObjectsInternal.add(s);
            case String[] arr when arr.length > MAX_STRING_ARRAY_LENGTH ->
                    throw new IllegalArgumentException("String array length exceeds maximum allowed length");
            case String[] arr -> _listOfObjectsInternal.add(arr);

            case Integer i -> _listOfObjectsInternal.add(i);

            case DataInput d -> _listOfObjectsInternal.add(d);

            default ->
                    throw new IllegalArgumentException("Unsupported data type: " + element.getClass().getSimpleName());
        }
    }

    public void add(Object element) {
        validateAndAddToArray(element);
    }

    public List<Object> getData() {
        return _listOfObjectsInternal;
    }

    @Override
    public String toString() {
        return _listOfObjectsInternal.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataInput other = (DataInput) o;
        if (_listOfObjectsInternal.size() != other._listOfObjectsInternal.size()) return false;

        for (int i = 0; i < _listOfObjectsInternal.size(); i++) {
            Object thisItem = _listOfObjectsInternal.get(i);
            Object otherItem = other._listOfObjectsInternal.get(i);

            // Both null or same object
            if (thisItem == otherItem) continue;

            // One null, one not null
            if (thisItem == null || otherItem == null) return false;

            // Both are arrays
            if (thisItem.getClass().isArray() && otherItem.getClass().isArray()) {
                if (!compareArrays(thisItem, otherItem)) return false;
            }
            // Regular objects
            else if (!Objects.equals(thisItem, otherItem)) {
                return false;
            }
        }

        return true;
    }

    private boolean compareArrays(Object array1, Object array2) {
        if (array1 instanceof String[] && array2 instanceof String[]) {
            return Arrays.equals((String[]) array1, (String[]) array2);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_listOfObjectsInternal);
    }
}