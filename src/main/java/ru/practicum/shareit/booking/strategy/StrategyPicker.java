package ru.practicum.shareit.booking.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.BadRequestException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StrategyPicker {
    private final List<AbstractBookingStrategy> strategies;

    public AbstractBookingStrategy pick(String state) {
        return strategies.stream()
                .filter(strategy -> strategy.getState().equals(state))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Strategy not found"));
    }
}
