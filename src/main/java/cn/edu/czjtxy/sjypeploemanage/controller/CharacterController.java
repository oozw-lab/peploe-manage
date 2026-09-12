package cn.edu.czjtxy.sjypeploemanage.controller;

import cn.edu.czjtxy.sjypeploemanage.common.Result;
import cn.edu.czjtxy.sjypeploemanage.entity.Character;
import cn.edu.czjtxy.sjypeploemanage.service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/character")
@CrossOrigin
public class CharacterController {

    @Autowired
    private CharacterService characterService;

    @GetMapping("/list")
    public Result<List<Character>> list(@RequestParam Long userId) {
        List<Character> list = characterService.list(userId);
        return Result.success(list);
    }

    @GetMapping("/options")
    public Result<List<Character>> options(@RequestParam Long userId) {
        List<Character> list = characterService.list(userId);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Character> getById(@PathVariable Long id) {
        Character character = characterService.getById(id);
        return Result.success(character);
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Character character) {
        if (character.getName() == null || character.getGender() == null) {
            return Result.error("角色名称和性别不能为空");
        }
        if (character.getStatus() == null) {
            character.setStatus(1);
        }
        characterService.add(character);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Character character) {
        characterService.update(character);
        return Result.success();
    }

    @PostMapping("/{id}/adjust")
    public Result<Void> adjust(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer wisdom = params.get("wisdom");
        Integer force = params.get("force");
        Integer social = params.get("social");
        Integer agility = params.get("agility");
        characterService.adjust(id, wisdom, force, social, agility);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        characterService.delete(id);
        return Result.success();
    }
}