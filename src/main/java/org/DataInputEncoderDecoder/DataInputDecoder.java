package org.DataInputEncoderDecoder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class DataInputDecoder {

    public DataInput decode(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);

        // Get byteBuffer from the byte array. Enforce the same byte order while
        // decoding the data.
        ByteBuffer dataBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);

        return decodeBytesInternal(dataBuffer);
    }

    private DataInput decodeBytesInternal(ByteBuffer dataBuffer) {
        DataInput result = new DataInput();

        // Get the total size of the elements in DataInput
        int dataInputSize = dataBuffer.getInt();

        for (int i = 0; i < dataInputSize; i++) {
            byte type = dataBuffer.get();

            switch (type) {
                case EncodeDecodeCommonConstants.STRING_TYPE -> {
                    // Get the string length.
                    int strLength = dataBuffer.getInt();

                    // Initialise byte array for the specified length.
                    byte[] strBytes = new byte[strLength];

                    // Read from the buffer and to DataInput.
                    dataBuffer.get(strBytes);
                    result.add(new String(strBytes, StandardCharsets.UTF_8));
                }
                case EncodeDecodeCommonConstants.INTEGER_TYPE -> result.add(dataBuffer.getInt());
                case EncodeDecodeCommonConstants.DATAINPUT_TYPE -> result.add(decodeBytesInternal(dataBuffer));
                case EncodeDecodeCommonConstants.STRING_ARRAY_TYPE -> {
                    // Get the length of the String array.
                    int strArrayLength = dataBuffer.getInt();

                    // Initialise str array according to the size.
                    String[] strArray = new String[strArrayLength];
                    for (int j = 0; j < strArrayLength; j++) {
                        // For each string get the length.
                        int elemLength = dataBuffer.getInt();

                        // Read appropriate bytes from the buffer and add to
                        // the array.
                        byte[] elemBytes = new byte[elemLength];
                        dataBuffer.get(elemBytes);
                        strArray[j] = new String(elemBytes, StandardCharsets.UTF_8);
                    }
                    // Add the RRy to the result.
                    result.add(strArray);
                }

                default -> throw new IllegalArgumentException("Unknown type: " + type);
            };
        }
        return result;
    }
}
