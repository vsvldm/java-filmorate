package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaServiceImpl implements MpaService {
    private final MpaRepository mpaRepository;

    @Override
    public Mpa findById(int mpaId) {
        return mpaRepository.getById(mpaId);
    }

    @Override
    public List<Mpa> findAll() {
        return new ArrayList<>(mpaRepository.values());
    }
}