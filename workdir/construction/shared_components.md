# Shared Components Design

## Common Data Models

### ESGHolding
```
- holdingId: String
- symbol: String  
- sector: String
- environmentalScore: Double
- socialScore: Double
- governanceScore: Double
- compositeScore: Double
- timestamp: LocalDateTime
```

### Portfolio
```
- portfolioId: String
- name: String
- holdings: List<ESGHolding>
- aggregateScores: ESGScores
- lastUpdated: LocalDateTime
```

## Cross-Cutting Concerns

### Logging Component
- **Structured Logging**: JSON format with correlation IDs
- **Log Levels**: ERROR, WARN, INFO, DEBUG
- **Centralized**: ELK stack for log aggregation

### Monitoring Component  
- **Health Checks**: Per-unit health endpoints
- **Metrics Collection**: Prometheus format
- **Alerting**: Grafana dashboards with alerts

### Security Components
- **Authentication Service**: JWT token validation
- **Authorization Service**: RBAC implementation
- **Audit Logger**: Security event tracking

## Configuration Management
- **Centralized Config**: Spring Cloud Config
- **Environment Variables**: For deployment-specific settings
- **Hot Reload**: Configuration updates without restart