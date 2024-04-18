package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.director.DirectorRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    private final DirectorRepository directorRepository;

    @Override
    public Director create(Director director) {
        String name = director.getName();

        if (name == null) {
            throw new BadRequestException("DirectorService: Имя не может быть null");
        }
        if (name.isBlank()) {
            throw new BadRequestException("DirectorService: Имя не может быть пустым");
        }
        log.debug("DirectorService.create: Создаем режиссера {}", name);
        Director stored = directorRepository.create(director);
        log.debug("DirectorService.create: Создан режиссер {}", stored.getName());
        return stored;
    }

    @Override
    public Director update(Director director) {
        directorRepository.findById(director.getId()).orElseThrow(() -> new NotFoundException(
                String.format("Режиссер с ID = %d не найден ", director.getId())));
        log.debug("DirectorService.update: Обновляем режиссера {}", director.getName());
        Director stored = directorRepository.update(director);
        log.debug("DirectorService.update: Обновлен режиссер {}", stored.getName());
        return stored;
    }

    @Override
    public Director getById(int id) {
        log.info("DirectorService.getById: Поиск режиссера с id =  {}.", id);
        return directorRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Режиссер с ID = %d не найден", id)));
    }

    @Override
    public List<Director> getAll() {
        log.info("DirectorService.getAll: Поиск  всех режиссеров ");
        return directorRepository.findAll();
    }

    @Override
    public void remove(int id) {
        log.info("DirectorService.remove: Режиссер c id = {} начато удаление ", id);
        directorRepository.remove(id);
        log.info("DirectorService.remove: Режиссер c id = {} удален ", id);
    }
}