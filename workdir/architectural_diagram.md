# ESG Data Platform - Architectural Diagram

## System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                           ESG DATA PLATFORM ARCHITECTURE                            │
└─────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   EXTERNAL      │    │     UNIT 1      │    │     UNIT 2      │    │     UNIT 3      │
│   DATA SOURCES  │    │ DATA PROCESSING │    │   PORTFOLIO     │    │      RISK       │
│                 │    │  & ESG SCORING  │    │   MONITORING    │    │   INTEGRATION   │
└─────────────────┘    └─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │                       │
         │                       │                       │                       │
         ▼                       ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   CSV FILES     │    │  MESSAGE QUEUE  │    │  EMAIL SERVICE  │    │  RISK SYSTEMS   │
│   /data/esg/    │    │   (Kafka/RMQ)   │    │     (SMTP)      │    │   (External)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                │
                                ▼
                    ┌─────────────────────────────────┐
                    │         UNIT 4 & 5              │
                    │    ANALYTICS & VISUALIZATION    │
                    │                                 │
                    │  ┌─────────────────────────────┐ │
                    │  │        WEB UI               │ │
                    │  │   (React/Angular/Vue)       │ │
                    │  └─────────────────────────────┘ │
                    └─────────────────────────────────┘
```

## Detailed Component Architecture

### Unit 1: Data Processing & ESG Scoring
```
┌─────────────────────────────────────────────────────────────────┐
│                    UNIT 1: DATA PROCESSING                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │ CSV Reader  │───▶│ Validator   │───▶│ESG Calculator│         │
│  │ (US-001)    │    │ (US-003)    │    │ (US-002)     │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│         │                   │                   │              │
│         ▼                   ▼                   ▼              │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │File Monitor │    │Data Quality │    │Score Storage│         │
│  │(US-020)     │    │   Reports   │    │(In-Memory)  │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│                                                │                │
│                                                ▼                │
│                                    ┌─────────────────┐          │
│                                    │ Message Publisher│          │
│                                    │ESGDataProcessed │          │
│                                    └─────────────────┘          │
└─────────────────────────────────────────────────────────────────┘
```

### Unit 2: Portfolio Monitoring & Alerting
```
┌─────────────────────────────────────────────────────────────────┐
│                UNIT 2: PORTFOLIO MONITORING                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │Threshold    │───▶│Portfolio    │───▶│Email        │         │
│  │Monitor      │    │Analytics    │    │Notification │         │
│  │(US-004)     │    │(US-005)     │    │(US-006)     │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│         │                   │                   │              │
│         ▼                   ▼                   ▼              │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │Threshold    │    │Metrics      │    │SMTP Client  │         │
│  │Config       │    │Calculator   │    │             │         │
│  │(US-018)     │    │             │    │             │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              Message Subscribers                        │   │
│  │  • ESGDataProcessed (from Unit 1)                      │   │
│  │  • ConfigurationUpdated                                │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### Unit 3: Risk Integration
```
┌─────────────────────────────────────────────────────────────────┐
│                   UNIT 3: RISK INTEGRATION                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │Risk Score   │───▶│Linear       │───▶│Risk         │         │
│  │Calculator   │    │Adjustment   │    │Assessment   │         │
│  │(US-007)     │    │Model        │    │Integration  │         │
│  └─────────────┘    │(US-008)     │    │(US-009)     │         │
│         │            └─────────────┘    └─────────────┘         │
│         ▼                   │                   │              │
│  ┌─────────────┐           ▼                   ▼              │
│  │External     │    ┌─────────────┐    ┌─────────────┐         │
│  │Risk Data    │    │Audit Trail  │    │Risk Storage │         │
│  │Feed         │    │Logger       │    │(In-Memory)  │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              Message Subscribers                        │   │
│  │  • ESGDataProcessed (from Unit 1)                      │   │
│  │  • BaseRiskScoresUpdated (External)                    │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### Unit 4: Core Analytics & Dashboards
```
┌─────────────────────────────────────────────────────────────────┐
│              UNIT 4: CORE ANALYTICS & DASHBOARDS               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │Role-Based   │───▶│Basic        │───▶│Drill-Down   │         │
│  │Dashboards   │    │Visualizations│    │Navigation   │         │
│  │(US-010)     │    │(US-011)     │    │(US-012)     │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│         │                   │                   │              │
│         ▼                   ▼                   ▼              │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │Dashboard    │    │Chart        │    │Navigation   │         │
│  │Engine       │    │Generator    │    │Controller   │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│                                                                 │
│  ┌─────────────┐    ┌─────────────┐                           │
│  │Sector-Level │    │System       │                           │
│  │ESG Views    │    │Parameter    │                           │
│  │(US-013)     │    │Management   │                           │
│  └─────────────┘    │(US-019)     │                           │
│                     └─────────────┘                           │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                   Web Frontend                          │   │
│  │  • React/Angular/Vue.js                                 │   │
│  │  • Chart.js/D3.js for visualizations                   │   │
│  │  • Responsive design (Bootstrap/Material-UI)           │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### Unit 5: Interactive Dashboard Features
```
┌─────────────────────────────────────────────────────────────────┐
│           UNIT 5: INTERACTIVE DASHBOARD FEATURES               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │Interactive  │───▶│Dashboard    │───▶│Real-Time    │         │
│  │Filtering    │    │Data Export  │    │Updates      │         │
│  │(US-014)     │    │(US-015)     │    │(US-016)     │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│         │                   │                   │              │
│         ▼                   ▼                   ▼              │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │Filter       │    │Export       │    │WebSocket    │         │
│  │Engine       │    │Generator    │    │Manager      │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│                                                                 │
│  ┌─────────────┐                                               │
│  │Interactive  │                                               │
│  │Chart        │                                               │
│  │Navigation   │                                               │
│  │(US-017)     │                                               │
│  └─────────────┘                                               │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              Advanced UI Components                     │   │
│  │  • Multi-select filters                                 │   │
│  │  • Export utilities (CSV, PNG, PDF)                    │   │
│  │  • Real-time data binding                               │   │
│  │  • Interactive charts (zoom, pan, hover)               │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

## Data Flow Architecture

### Primary Data Flow
```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   CSV       │────▶│   Unit 1    │────▶│ Message     │────▶│Units 2,3,4,5│
│   Files     │     │Data Process │     │   Queue     │     │ Consumers   │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘
                           │
                           ▼
                    ┌─────────────┐
                    │ ESG Scores  │
                    │ Calculated  │
                    │ (E,S,G,Comp)│
                    └─────────────┘
```

### Event-Driven Message Flow
```
Unit 1 Events:
├── ESGDataProcessed ────────┬─────────────┬─────────────┬─────────────┐
├── DataValidationFailed     │             │             │             │
└── FileProcessingCompleted  │             │             │             │
                             ▼             ▼             ▼             ▼
                        ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐
                        │ Unit 2  │  │ Unit 3  │  │ Unit 4  │  │ Unit 5  │
                        └─────────┘  └─────────┘  └─────────┘  └─────────┘
                             │             │             │             │
                             ▼             ▼             ▼             ▼
Unit 2 Events:          Unit 3 Events:    Unit 4 Events:    Unit 5 Events:
├── ThresholdBreach     ├── RiskScores    ├── Dashboard     ├── FilterApplied
├── EmailSent           ├── RiskAdjust    ├── DrillDown     ├── DataExported
└── AnalyticsUpdated    └── RiskCalcFail  └── ConfigUpdate  └── ChartInteract
```

## Technology Stack

### Backend Technologies
```
┌─────────────────────────────────────────────────────────────────┐
│                        BACKEND STACK                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Programming Languages:                                         │
│  ├── Java/Spring Boot (Unit 1, 2, 3)                          │
│  ├── Python/FastAPI (Alternative for data processing)          │
│  └── Node.js/Express (Unit 4, 5 - API layer)                  │
│                                                                 │
│  Message Queue:                                                 │
│  ├── Apache Kafka (Preferred for high throughput)              │
│  ├── RabbitMQ (Alternative for simpler setup)                  │
│  └── AWS SQS (Cloud-native option)                             │
│                                                                 │
│  Data Storage:                                                  │
│  ├── In-Memory (MVP): HashMap, ConcurrentHashMap               │
│  ├── Redis (Caching layer)                                     │
│  └── PostgreSQL (Future persistent storage)                    │
│                                                                 │
│  Configuration:                                                 │
│  ├── YAML/Properties files                                     │
│  ├── Environment variables                                     │
│  └── Spring Cloud Config (Centralized config)                 │
└─────────────────────────────────────────────────────────────────┘
```

### Frontend Technologies
```
┌─────────────────────────────────────────────────────────────────┐
│                       FRONTEND STACK                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Web Framework:                                                 │
│  ├── React.js (Recommended for component reusability)          │
│  ├── Angular (Alternative for enterprise features)             │
│  └── Vue.js (Alternative for simplicity)                       │
│                                                                 │
│  UI Components:                                                 │
│  ├── Material-UI / Ant Design (Component library)              │
│  ├── Bootstrap (Responsive grid system)                        │
│  └── Styled Components (CSS-in-JS)                             │
│                                                                 │
│  Data Visualization:                                            │
│  ├── Chart.js (Simple charts and graphs)                       │
│  ├── D3.js (Advanced interactive visualizations)               │
│  └── Recharts (React-specific charting)                        │
│                                                                 │
│  State Management:                                              │
│  ├── Redux/Redux Toolkit (Complex state)                       │
│  ├── Context API (Simple state)                                │
│  └── Zustand (Lightweight alternative)                         │
│                                                                 │
│  Real-time Updates:                                             │
│  ├── WebSockets (Real-time data push)                          │
│  ├── Server-Sent Events (One-way updates)                      │
│  └── Socket.io (Full-duplex communication)                     │
└─────────────────────────────────────────────────────────────────┘
```

### Infrastructure & DevOps
```
┌─────────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE STACK                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Containerization:                                              │
│  ├── Docker (Application containers)                            │
│  ├── Docker Compose (Local development)                        │
│  └── Kubernetes (Production orchestration)                     │
│                                                                 │
│  API Gateway:                                                   │
│  ├── Kong (Open source)                                        │
│  ├── AWS API Gateway (Cloud-native)                            │
│  └── Nginx (Reverse proxy/Load balancer)                       │
│                                                                 │
│  Monitoring & Logging:                                          │
│  ├── ELK Stack (Elasticsearch, Logstash, Kibana)              │
│  ├── Prometheus + Grafana (Metrics)                            │
│  └── Jaeger (Distributed tracing)                              │
│                                                                 │
│  CI/CD:                                                         │
│  ├── Jenkins (Build automation)                                │
│  ├── GitLab CI/CD (Integrated pipeline)                        │
│  └── GitHub Actions (Cloud-based CI/CD)                        │
└─────────────────────────────────────────────────────────────────┘
```

## Security Architecture
```
┌─────────────────────────────────────────────────────────────────┐
│                      SECURITY LAYERS                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Authentication & Authorization:                                │
│  ├── OAuth 2.0 / OpenID Connect                                │
│  ├── JWT Tokens (Stateless authentication)                     │
│  └── Role-Based Access Control (RBAC)                          │
│                                                                 │
│  Data Security:                                                 │
│  ├── TLS 1.3 (Data in transit)                                 │
│  ├── AES-256 (Data at rest encryption)                         │
│  └── PII Masking (Sensitive data protection)                   │
│                                                                 │
│  Network Security:                                              │
│  ├── VPC/Private Networks                                      │
│  ├── Firewall Rules                                            │
│  └── API Rate Limiting                                         │
└─────────────────────────────────────────────────────────────────┘
```

## Deployment Architecture
```
┌─────────────────────────────────────────────────────────────────┐
│                    DEPLOYMENT TOPOLOGY                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │   Unit 1    │    │   Unit 2    │    │   Unit 3    │         │
│  │ Container   │    │ Container   │    │ Container   │         │
│  │   (Pod)     │    │   (Pod)     │    │   (Pod)     │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│                                                                 │
│  ┌─────────────┐    ┌─────────────┐                           │
│  │   Unit 4    │    │   Unit 5    │                           │
│  │ Container   │    │ Container   │                           │
│  │   (Pod)     │    │   (Pod)     │                           │
│  └─────────────┘    └─────────────┘                           │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              Shared Infrastructure                      │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │   │
│  │  │   Kafka     │  │    Redis    │  │   Nginx     │     │   │
│  │  │  Cluster    │  │   Cache     │  │Load Balancer│     │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘     │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

## Performance Characteristics

### Scalability Targets
- **Unit 1**: Process 10,000 holdings in <5 minutes
- **Unit 2**: Monitor 50 portfolios concurrently
- **Unit 3**: Calculate risk adjustments for 1,000 holdings in <1 minute
- **Unit 4**: Support 100 concurrent dashboard users
- **Unit 5**: Handle 50 concurrent interactive sessions

### Availability Requirements
- **System Uptime**: 99.5% during business hours
- **Dashboard Response**: <3 seconds for data loading
- **Real-time Updates**: <30 seconds for data propagation
- **Email Alerts**: <15 minutes for threshold breaches

This architectural diagram provides a comprehensive view of the ESG Data Platform's structure, data flow, and technology choices based on the user stories and requirements defined in the overview document.