package com.example.myapplication;

import java.util.List;

public class item {
        public Coord coord;
        List<Weather> weather;
        public Main main;
        public Wind wind;
        public Sys sys;
        public String name;

        public class Coord{
            public double lon;
            public double lat;
        }
        public class Weather{
            public String main;
            public String description;
            public String icon;
        }

        public class Main{
            public float temp;
            public float humidity;

        }
        public class Wind{
            public String speed;
            public String deg;

        }
        public class Sys{
            public String country;
        }


}
