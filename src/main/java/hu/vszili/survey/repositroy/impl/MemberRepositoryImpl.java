package hu.vszili.survey.repositroy.impl;

import com.opencsv.bean.CsvToBeanBuilder;
import hu.vszili.survey.data.Member;
import hu.vszili.survey.exception.CsvReadingException;
import hu.vszili.survey.repositroy.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class MemberRepositoryImpl implements MemberRepository {

    @Value("${csv.base-path}")
    private String csvBasePath;

    private final Map<Long, Member> members = new ConcurrentHashMap<>();

    @PostConstruct
    private void init() {
        List<Member> memberList = this.readMembers();
        this.members.putAll(
                memberList.stream().collect(
                        Collectors.toMap(Member::getId, Function.identity())));
        log.info("Members loaded: {}", members.size());
    }

    private List<Member> readMembers() {
        try (FileReader fileReader = new FileReader(csvBasePath + "Members.csv")) {
            return new CsvToBeanBuilder<Member>(fileReader).withType(Member.class).build().parse();
        } catch (IOException | IllegalStateException e) {
            throw new CsvReadingException("Error reading members file: " + e.getMessage());
        }
    }

    @Override
    public Optional<Member> findById(Long memberId) {
        return Optional.of(members.get(memberId));
    }

    @Override
    public boolean existsById(Long surveyId) {
        return members.containsKey(surveyId);
    }

    @Override
    public Map<Long, Member> getMembers() {
        return new HashMap<>(this.members);
    }

}
