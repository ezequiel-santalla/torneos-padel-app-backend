package com.eze_dev.torneos.strategy.tournament;

import com.eze_dev.torneos.types.TournamentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TournamentStrategyFactory {

    private final QuadrangularStrategy quadrangularStrategy;

    public TournamentStrategy getStrategy(TournamentType type) {
        return switch (type) {
            case QUADRANGULAR -> quadrangularStrategy;
            case KNOCKOUT, ROUND_ROBIN -> throw new UnsupportedOperationException("Strategy not implemented yet for type: " + type);
        };
    }
}

