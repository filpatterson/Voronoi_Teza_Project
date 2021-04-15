package Globals;

public class YandexMapsRequester {
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

    public static String getCompleteRequestURL(double latitude, double longitude, double latitudeLength, double longitudeLength, int imageSizeX, int imageSizeY) {
        return YANDEX_STATIC_MAPS_LINK +
                COORDINATES + longitude + "," + latitude +
                SEPARATOR +
                BORDERS + longitudeLength + "," + latitudeLength +
                SEPARATOR +
                SIZE + imageSizeX + "," + imageSizeY +
                SEPARATOR +
                SCHEMA_MAP_TYPE;
    }
}
