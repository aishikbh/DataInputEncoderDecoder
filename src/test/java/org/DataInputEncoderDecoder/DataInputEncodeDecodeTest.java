package org.DataInputEncoderDecoder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataInputEncodeDecodeTest {

    @Test
    public void testBasicEncoderDecoderFunctionality() {
        DataInputEncoder encoder = new DataInputEncoder();
        DataInputDecoder decoder = new DataInputDecoder();

        // Case 1 : Example from the problem statement.
        DataInput data1 = new DataInput("foo", new DataInput("bar", 42));
        Assertions.assertEquals(data1, decoder.decode(encoder.encode(data1)));

        // Case 2 : Just Strings.
        DataInput data2 = new DataInput("hello", "world");
        Assertions.assertEquals(data2, decoder.decode(encoder.encode(data2)));

        // Case 3 : Just Integers.
        DataInput data3 = new DataInput(1, 2, 3, 4, 5);
        Assertions.assertEquals(data3, decoder.decode(encoder.encode(data3)));

        // Case 4 : Just String[].
        DataInput data4 = new DataInput(new String[]{"Hitchhiker's", "guide"},
                new String[]{"to", "the"}, new String[]{"galaxy"});
        Assertions.assertEquals(data4, decoder.decode(encoder.encode(data4)));

        // Case 5 : Mixture of all supported data types.
        DataInput data5 = new DataInput(data1, data2, data3, data4);
        Assertions.assertEquals(data5, decoder.decode(encoder.encode(data5)));
    }

    @Test
    public void testDataInputTypesAndLimits() {
        // Case 1 : Test Integer array. Currently not supported should throw an
        // Exception.
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DataInput(new int[]{1, 2, 3}));


        // Case 2 : Test length of str[] more than 1000000. Should throw an exception.
        String[] str = new String[1000001];
        for (int i = 0; i < 1000001; i++) {
            str[i] = "a";
        }

        // Should throw an exception.
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DataInput((Object) str));


        // Case 3 :  Test str length more than 1000.
        StringBuilder s = new StringBuilder();
        s.append("a".repeat(1001));

        // Should throw an error.
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DataInput((Object) s));
    }
}