package com.laisontech.googlesearch.entity;

import java.util.List;

/**
 * Created by SDP on 2018/4/23.
 */

public class SearchTextInfo {
    public List<Results> results;
    public String status; //OK


    public class Results {
        public String formatted_address;
        public Geometry geometry;
        public String icon;
        public String id;
        public String name;
        public List<Photos> photos;
        public String place_id;
        public float rating;
        public String reference;

        @Override
        public String toString() {
            return "Results{" +
                    "formatted_address='" + formatted_address + '\'' +
                    ", geometry=" + geometry +
                    ", icon='" + icon + '\'' +
                    ", id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", photos=" + photos +
                    ", place_id='" + place_id + '\'' +
                    ", rating='" + rating + '\'' +
                    ", reference='" + reference + '\'' +
                    '}';
        }
    }

    public class Geometry {
        public Location location;
        public Viewport viewport;

        @Override
        public String toString() {
            return "Geometry{" +
                    "location=" + location +
                    ", viewport=" + viewport +
                    '}';
        }
    }

    public class Photos {
        public int height;
        public String photo_reference;
        public int width;

        @Override
        public String toString() {
            return "Photos{" +
                    "height=" + height +
                    ", photo_reference='" + photo_reference + '\'' +
                    ", width=" + width +
                    '}';
        }
    }

    public class Location {
        public double lat;
        public double lng;
        @Override
        public String toString() {
            return "Southwest{" +
                    "lat=" + lat +
                    ", lng=" + lng +
                    '}';
        }
    }

    public class Viewport {
        public Northeast northeast;
        public Southwest southwest;

        @Override
        public String toString() {
            return "Viewport{" +
                    "northeast=" + northeast +
                    ", southwest=" + southwest +
                    '}';
        }
    }

    public class Northeast {
        public double lat;
        public double lng;
        @Override
        public String toString() {
            return "Southwest{" +
                    "lat=" + lat +
                    ", lng=" + lng +
                    '}';
        }
    }

    public class Southwest {
        public double lat;
        public double lng;

        @Override
        public String toString() {
            return "Southwest{" +
                    "lat=" + lat +
                    ", lng=" + lng +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SearchTextInfo{" +
                ", results=" + results +
                ", status='" + status + '\'' +
                '}';
    }
}
