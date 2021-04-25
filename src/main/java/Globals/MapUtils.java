package Globals;

public class MapUtils {
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

    public static void setMapHandlerParameters(double latitude, double longitude,
                                               double latitudeCenterToBorder, double longitudeCenterToBorder) {
        //  init all characteristics of the area
        MapUtils.centerLatitude = latitude;
        MapUtils.centerLongitude = longitude;
        MapUtils.latitudeRadius = latitudeCenterToBorder;
        MapUtils.longitudeRadius = longitudeCenterToBorder;

        //  find "resolution"
        MapUtils.latitudeResolution = (latitudeCenterToBorder * 2) / Utils.yLimit;
        MapUtils.longitudeResolution = (longitudeCenterToBorder * 2) / Utils.xLimit;
    }

    /**
     * Form string-formatted URL that will be used for performing request
     * @return string-formatted URL for making request to the Yandex.Maps Static API
     */
    public static String getCompleteRequestURL() {
        //  give string-formatted URL that will take all given characteristics
        return YANDEX_STATIC_MAPS_LINK +
                COORDINATES + centerLongitude + "," + centerLatitude +  //  center of the map coordinates
                SEPARATOR +
                BORDERS + longitudeRadius + "," + latitudeRadius +  //  radius of coverage by map
                SEPARATOR +
                SIZE + 450 + "," + 450 +    //  size of the image (optimal value - 450x450, possible - 650x450
                SEPARATOR +
                SCHEMA_MAP_TYPE;
    }
}
