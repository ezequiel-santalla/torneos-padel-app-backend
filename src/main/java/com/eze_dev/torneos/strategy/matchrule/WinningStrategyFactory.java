package com.eze_dev.torneos.strategy.matchrule;

import com.eze_dev.torneos.types.WinningMatchRuleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WinningStrategyFactory {

    private final OneSetTo6Strategy oneSetTo6Strategy;

    public WinningStrategy getStrategy(WinningMatchRuleType ruleType) {
        return switch (ruleType) {
            case ONE_SET_TO_6 -> oneSetTo6Strategy;
            case ONE_SET_TO_5 -> throw new UnsupportedOperationException("Strategy for ONE_SET_TO_5 not implemented yet");
            case ONE_SET_TO_8 -> throw new UnsupportedOperationException("Strategy for ONE_SET_TO_8 not implemented yet");
            case BEST_OF_3_SETS -> throw new UnsupportedOperationException("Strategy for BEST_OF_3_SETS not implemented yet");
        };
    }
}

