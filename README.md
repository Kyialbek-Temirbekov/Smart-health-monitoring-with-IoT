# Smart Health Monitoring System with IoT

## Description
This project is a smart health monitoring system that utilizes IoT-enabled smartwatches to collect real-time health metrics. The system processes and stores health data in the cloud, performs statistical analysis and machine learning for anomaly detection, and provides automated emergency alerts with real-time location sharing when critical health events are detected.

Key features include:
- Real-time health metric tracking (heart rate, oxygen saturation, air quality, etc.)
- Secure IoT communication using MQTT protocol
- Time-series data handling and anomaly detection
- Automated alert system with different severity levels
- Predictive analysis using machine learning models
- Mobile application integration for data visualization

## Technologies Used
- **Backend**: Java, Spring Boot
- **Database**: PostgreSQL with TimescaleDB extension
- **Caching**: Redis
- **IoT Communication**: MQTT (HiveMQ broker)
- **Security**: JWT authentication, data encryption
- **Machine Learning**: Weka library for predictive analysis
- **Other**: Swagger for API documentation, Lombok for boilerplate reduction

## Usage
The system provides REST APIs for:
- User authentication and management
- Device registration and management
- Health metric data collection and interpretation
- Recommendations based on health data analysis
- Configuration management

The API documentation is available at `/swagger-ui.html` when the application is running.

## System Architecture
The system consists of:
1. IoT devices (smartwatches) that collect and transmit health data
2. Cloud backend that processes and stores the data
3. Mobile application for data visualization and user interaction
4. Emergency notification system for critical health events

The system classifies health status into four levels (Normal, Warning, Critical, Emergency) and triggers appropriate responses for each level.
