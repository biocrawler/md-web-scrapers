package org.perpetualnetworks.mdcrawlerconsumer.utils.lzw;

/**
 * Specifies that implementing classes support data compression and decompression.
 *
 * @author Saul Johnson, Alex Mullen, Lee Oliver
 */
public abstract class Compressor {

    /**
     * Gets the name of the compressor.
     *
     * @return the name of the compressor
     */
    public abstract String getName();

    /**
     * Compresses an array of bytes.
     *
     * @param data the byte array to compress
     * @return a compressed array of bytes
     */
    public abstract byte[] compress(byte[] data);

    /**
     * Decompresses an array of bytes.
     *
     * @param data the byte array to decompress
     * @return a decompressed array of bytes
     */
    public abstract byte[] decompress(byte[] data);

}
