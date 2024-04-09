package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.director.DirectorRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService{
    private final DirectorRepository directorRepository;
    @Override
    public Director create(Director director) {
        log.debug("DirectorService: Создаем режиссера {}", director.getName());
        Director d = directorRepository.create(director);
        log.debug("DirectorService: Создан режиссер {}", d.getName());
        return d;
    }

    @Override
    public Director update(Director director) {
        directorRepository.getById(director.getId()).orElseThrow(() -> new NotFoundException(
                String.format("Режиссер с ID = %d не найден ", director.getId())));
        log.debug("DirectorService: Обновляем режиссера {}", director.getName());
        Director d = directorRepository.update(director);
        log.debug("DirectorService: Обновлен режиссер {}", d.getName());
        return d;
    }

    @Override
    public Director getById(int id) {
        return directorRepository.getById(id).orElseThrow(() -> new NotFoundException(
                String.format("Режиссер с ID = %d не найден", id)));
    }

    @Override
    public List<Director> getAll() {
        return directorRepository.getAll();
    }

    @Override
    public void remove(int id) {
        directorRepository.remove(id);
    }
}
