//package com.example.demo;
//
//@Component
//public class DynamicRouteLoader {
//
//    @Autowired
//    private RouteDefinitionWriter routeDefinitionWriter;
//
//    @Autowired
//    private ApplicationEventPublisher publisher;
//
//    @Autowired
//    private NacosRouteFetcher fetcher;
//
//    public void refreshRoutes() {
//        try {
//            List<CustomRouteDefinition> routeList = fetcher.loadRoutes();
//
//            // 清空已有路由（可选）
//            // routeDefinitionWriter.delete(...)
//
//            for (CustomRouteDefinition route : routeList) {
//                RouteDefinition definition = new RouteDefinition();
//                definition.setId(route.getName());
//
//                PredicateDefinition predicate = new PredicateDefinition();
//                predicate.setName("Path");
//                predicate.addArg("pattern", route.getPath());
//
//                PredicateDefinition methodPredicate = new PredicateDefinition();
//                methodPredicate.setName("Method");
//                methodPredicate.addArg("_genkey_0", route.getMethod());
//
//                definition.setPredicates(Arrays.asList(predicate, methodPredicate));
//                URI uri = URI.create("lb://" + route.getServiceId());
//                definition.setUri(uri);
//
//                routeDefinitionWriter.save(Mono.just(definition)).subscribe();
//            }
//
//            publisher.publishEvent(new RefreshRoutesEvent(this));
//        } catch (Exception e) {
//            throw new RuntimeException("Error refreshing gateway routes", e);
//        }
//    }
//}
