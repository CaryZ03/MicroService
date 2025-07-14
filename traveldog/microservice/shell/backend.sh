#!/bin/bash
tar -cvf backend.tar Hotel_Service/target/*.jar Hotel_Service/target/Dockerfile Location_Service/target/*.jar Location_Service/target/Dockerfile Order_Service/target/*.jar Order_Service/target/Dockerfile Train_Service/target/*.jar Train_Service/target/Dockerfile User_Service/target/*.jar User_Service/target/Dockerfile compose.yaml
