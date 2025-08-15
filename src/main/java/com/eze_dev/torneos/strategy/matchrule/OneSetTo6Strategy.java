package com.eze_dev.torneos.strategy.matchrule;

import com.eze_dev.torneos.model.Match;
import com.eze_dev.torneos.model.Pair;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OneSetTo6Strategy implements WinningStrategy {

    private static final int GAMES_TO_WIN = 6;
    private static final int MINIMUM_LEAD = 2;
    private static final int MAX_GAMES = 7;

    @Override
    public boolean hasWinner(Match match) {
        if (match.getPair1Score() < 0 || match.getPair2Score() < 0) return false;

        int diff = Math.abs(match.getPair1Score() - match.getPair2Score());

        if (match.getPair1Score() >= 5 && match.getPair2Score() >= 5) {
            return (match.getPair1Score() == MAX_GAMES || match.getPair2Score() == MAX_GAMES) && diff == 1;
        }

        return (match.getPair1Score() >= GAMES_TO_WIN || match.getPair2Score() >= GAMES_TO_WIN) && diff >= MINIMUM_LEAD;
    }

    @Override
    public Optional<Pair> getWinner(Match match) {
        if (!hasWinner(match)) {
            return Optional.empty();
        }

        return match.getPair1Score() > match.getPair2Score() ? Optional.of(match.getPair1()) : Optional.of(match.getPair2());
    }

    @Override
    public boolean isValidResult(Match match) {
        if (match.getPair1Score() < 0 || match.getPair2Score() < 0) return false;
        if (match.getPair1Score() > MAX_GAMES || match.getPair2Score() > MAX_GAMES) return false;

        if (match.getPair1Score() == 7 && match.getPair2Score() == 6) return true;
        if (match.getPair2Score() == 7 && match.getPair1Score() == 6) return true;

        int diff = Math.abs(match.getPair1Score() - match.getPair2Score());

        return (match.getPair1Score() >= 6 || match.getPair2Score() >= 6) && diff >= 2;
    }
}
