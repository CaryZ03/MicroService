//package com.example.demo;
//
//@Service
//public class NacosRouteFetcher {
//
//    @Autowired
//    private ConfigService configService;
//
//    public List<CustomRouteDefinition> loadRoutes() {
//        try {
//            String config = configService.getConfig("gateway-routes.json", "DEFAULT_GROUP", 5000);
//            return new ObjectMapper().readValue(config, new TypeReference<List<CustomRouteDefinition>>() {});
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to load routes from Nacos", e);
//        }
//    }
//}
