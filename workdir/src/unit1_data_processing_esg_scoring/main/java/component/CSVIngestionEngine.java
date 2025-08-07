package component;

import model.ESGHolding;
import model.ProcessingMetrics;
import service.CSVReader;
import service.DataExtractor;
import service.FormatValidator;

import java.util.List;

public class CSVIngestionEngine {
    private final CSVReader csvReader = new CSVReader();
    private final DataExtractor dataExtractor = new DataExtractor();
    private final FormatValidator formatValidator = new FormatValidator();

    public IngestionResult ingestFile(String filePath) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Validate file format
            if (!formatValidator.validateFormat(filePath)) {
                return new IngestionResult(false, "Invalid CSV format", null, null);
            }
            
            // Read and parse CSV
            List<String[]> csvData = csvReader.readCSV(filePath);
            if (csvData.isEmpty()) {
                return new IngestionResult(false, "Empty CSV file", null, null);
            }
            
            // Extract ESG holdings
            List<ESGHolding> holdings = dataExtractor.extractHoldings(csvData);
            
            // Calculate metrics
            long processingTime = System.currentTimeMillis() - startTime;
            ProcessingMetrics metrics = new ProcessingMetrics(
                processingTime,
                holdings.size() * 1000L / Math.max(processingTime, 1),
                Runtime.getRuntime().totalMemory() / (1024 * 1024),
                1
            );
            
            return new IngestionResult(true, "Successfully ingested " + holdings.size() + " holdings", holdings, metrics);
            
        } catch (Exception e) {
            return new IngestionResult(false, "Ingestion failed: " + e.getMessage(), null, null);
        }
    }

    public static class IngestionResult {
        private final boolean success;
        private final String message;
        private final List<ESGHolding> holdings;
        private final ProcessingMetrics metrics;

        public IngestionResult(boolean success, String message, List<ESGHolding> holdings, ProcessingMetrics metrics) {
            this.success = success;
            this.message = message;
            this.holdings = holdings;
            this.metrics = metrics;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public List<ESGHolding> getHoldings() { return holdings; }
        public ProcessingMetrics getMetrics() { return metrics; }
    }
}