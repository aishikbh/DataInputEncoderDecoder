package org.DataInputEncoderDecoder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class DataInputEncoder {

    /*
     The idea is to encode the data in the following way :
     [length of list of objects][type identifier]<optional 1 : array length(in case of str[])>
     <optional 2 : actual length of the objects(in case of String or String[])

     Integer : add the type identifier as the first byte and then
     the value.
     example encoding for integer 12 : [0][12].

     String : add the type identifier as the first byte, length of
     the string as the second byte and then the string.
     example encoding for string "java" : [1][4]["java"].

     String[] : add the type identifier as the first byte, length of
     the array size in second byte, now for each of strings in the array we
     encode the strings as we do strings.
     example encoding for string[] "java", "python" : [2][2][4]["java"][6]["python"].

     DataInput : add the type identifier as the first byte, length of the input as the second byte,
     and then recursively encode the DataInput.
     example encoding for DataInput{"foo", DataInput{"bar", 42}}
     [2][1][3]["foo"][2][2][1][3]["bar"][1][42]
     |______________||________________________|
            |                   |
           foo          DataInput{"bar", 42}
      Time complexity = O(n) where n being the number of elements in DataInput including the nested ones.
                        Even though the string length and array length are variable, they are bounded. So
                        on an average we will have O(n).
      Space complexity = O(n) we are using the buffer as extra space which is equal to the total size of the input data
                         plus the length and type indicators.
     */

    public String encode(DataInput dataInput) {
        int bufferLength = calculateBufferLength(dataInput);

        // Wrap the buffer on a byte[] to eliminate an extra decode step while
        // converting to a String. Also enforce byte order so that there are no
        // discrepancies while encoding/decoding data across different systems.
        ByteBuffer dataBuffer = ByteBuffer.wrap(new byte[bufferLength]).order(ByteOrder.BIG_ENDIAN);

        // Encode the data.
        encodeDataInternal(dataInput, dataBuffer);

        // Convert the underlying bytearray into string and return.
        return new String(dataBuffer.array(), StandardCharsets.UTF_8);
    }

    private int calculateBufferLength(DataInput dataInput) {
        // Size indicator (4 bytes) + type indicator (1 byte per item) + data size
        int size = 4;

        // Get the total space needed to put it into the buffer including nested DataInput.
        for (Object item : dataInput.getData()) {
            size += 1; // Type indicator
            switch (item) {
                case String str -> {
                    byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
                    size += 4 + bytes.length; // Length (4 bytes) + string bytes
                }
                case Integer ignored -> size += 4; // Integer (4 bytes)
                case DataInput nestedInput -> size += calculateBufferLength(nestedInput);
                case String[] array -> {
                    size += 4; // Array length (4 bytes)
                    for (String str : array) {
                        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
                        size += 4 + bytes.length; // String length (4 bytes) + string bytes
                    }
                }
                default -> throw new IllegalArgumentException("Unsupported type: " + item.getClass().getSimpleName());
            }
        }
        return size;
    }

    private void encodeDataInternal(DataInput dataInput, ByteBuffer dataBuffer) {
        dataBuffer.putInt(dataInput.getData().size());

        for (Object item : dataInput.getData()) {
            switch (item) {
                case String str -> {
                    dataBuffer.put((byte) EncodeDecodeCommonConstants.STRING_TYPE); // Type indicator for String
                    byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
                    dataBuffer.putInt(bytes.length);
                    dataBuffer.put(bytes);
                }
                case Integer intValue -> {
                    dataBuffer.put((byte) EncodeDecodeCommonConstants.INTEGER_TYPE); // Type indicator for Integer
                    dataBuffer.putInt(intValue);
                }
                case DataInput nestedInput -> {
                    dataBuffer.put((byte) EncodeDecodeCommonConstants.DATAINPUT_TYPE); // Type indicator for nested DataInput
                    encodeDataInternal(nestedInput, dataBuffer);
                }
                case String[] array -> {
                    dataBuffer.put((byte) EncodeDecodeCommonConstants.STRING_ARRAY_TYPE); // Type indicator for String array
                    dataBuffer.putInt(array.length);
                    for (String str : array) {
                        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
                        dataBuffer.putInt(bytes.length);
                        dataBuffer.put(bytes);
                    }
                }
                default -> throw new IllegalArgumentException("Unsupported type: " + item.getClass().getSimpleName());
            }
        }
    }
}