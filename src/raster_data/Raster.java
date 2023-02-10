package raster_data;

import java.util.Optional;

/**
 * Represent 2D raster image with pixels of the given type
 *
 * @param <P> pixel type
 */


public interface Raster<P> {

    /**
     * Returns the number of columns in this Raster
     *
     * @return number of columns
     */
    int getWidth();

    /**
     * Return the number of rows in this Raster
     *
     * @return number of rows
     */
    int getHeight();

    /**
     * Update the pixel value at the given address
     *
     * @param x     column address
     * @param y     row address
     * @param pixel new pixel value
     * @return true if updated; false otherwise
     */
    boolean setPixel(int x, int y, P pixel);

    /**
     * Return an Optional of the pixel value at the given address
     *
     * @param x column address
     * @param y row address
     * @return Optional of pixel if [x, y] is a valid address; empty Optional otherwise
     */
    Optional<P> getPixel(int x, int y);

    /**
     * Updates all values in this Raster to the background pixel value
     */
    void clear();

    /**
     * @param x column address
     * @param y row address
     * @return returns true if the address is within window; false otherwise
     */
    default boolean isValidAddress(int x, int y) {
        return x >= 0 && y >= 0 && y < getHeight() && x < getWidth();
    }

}
