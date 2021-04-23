package Globals;

public class MapHandler {
    //  center geographical coordinates
    public static double centerLatitude;
    public static double centerLongitude;

    //  distance from center to border by geographical coordinates
    public static double latitudeRadius;
    public static double longitudeRadius;

    //  coefficients for mapping geographical coordinates to the image cartesian coordinates
    public static double longitudeResolution;
    public static double latitudeResolution;

    //  link to the Yandex Maps static API
    public static final String YANDEX_STATIC_MAPS_LINK = "https://static-maps.yandex.ru/1.x/?";

    //  choose map scheme
    public static final String SCHEMA_MAP_TYPE = "l=map";

    //  set size of picture (example: "size=300,300")
    public static final String SIZE = "size=";

    //  separator that separates arguments of request
    public static final String SEPARATOR = "&";

    //  coordinate of the center of region
    public static final String COORDINATES = "ll=";

    //  show sector length in longitude and latitude
    public static final String BORDERS = "spn=";

    /**
     * Form string-formatted URL that will be used for performing request
     * @param latitude center latitude
     * @param longitude center longitude
     * @param latitudeCenterToBorder latitude distance from center to border
     * @param longitudeCenterToBorder longitude distance from center to border
     * @param imageSizeX X image size
     * @param imageSizeY Y image size
     * @return
     */
    public static String getCompleteRequestURL(
            double latitude, double longitude,
            double latitudeCenterToBorder, double longitudeCenterToBorder,
            int imageSizeX, int imageSizeY
    ) {
        //  init all characteristics of the area
        MapHandler.centerLatitude = latitude;
        MapHandler.centerLongitude = longitude;
        MapHandler.latitudeRadius = latitudeCenterToBorder;
        MapHandler.longitudeRadius = longitudeCenterToBorder;

        //  find "resolution"
        MapHandler.latitudeResolution = (latitudeCenterToBorder * 2) / Parameters.yLimit;
        MapHandler.longitudeResolution = (longitudeCenterToBorder * 2) / Parameters.xLimit;

        //  give string-formatted URL that will take all given characteristics
        return YANDEX_STATIC_MAPS_LINK +
                COORDINATES + longitude + "," + latitude +
                SEPARATOR +
                BORDERS + longitudeCenterToBorder + "," + latitudeCenterToBorder +
                SEPARATOR +
                SIZE + imageSizeX + "," + imageSizeY +
                SEPARATOR +
                SCHEMA_MAP_TYPE;
    }
}
