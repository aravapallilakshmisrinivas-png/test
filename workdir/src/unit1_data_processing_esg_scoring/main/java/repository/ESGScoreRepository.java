package repository;

import model.ESGHolding;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ESGScoreRepository {
    private final ConcurrentHashMap<String, ESGHolding> holdings = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> portfolioIndex = new ConcurrentHashMap<>();

    public void save(ESGHolding holding) {
        holdings.put(holding.getHoldingId(), holding);
        portfolioIndex.computeIfAbsent("PORTFOLIO_001", k -> ConcurrentHashMap.newKeySet())
                     .add(holding.getHoldingId());
    }

    public Optional<ESGHolding> findById(String holdingId) {
        return Optional.ofNullable(holdings.get(holdingId));
    }

    public List<ESGHolding> findByPortfolio(String portfolioId) {
        Set<String> holdingIds = portfolioIndex.get(portfolioId);
        if (holdingIds == null) return new ArrayList<>();
        
        return holdingIds.stream()
                .map(holdings::get)
                .filter(Objects::nonNull)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<ESGHolding> findAll() {
        return new ArrayList<>(holdings.values());
    }

    public void clear() {
        holdings.clear();
        portfolioIndex.clear();
    }
}