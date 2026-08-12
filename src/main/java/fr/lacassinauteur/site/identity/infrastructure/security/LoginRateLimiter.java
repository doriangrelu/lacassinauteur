package fr.lacassinauteur.site.identity.infrastructure.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Anti brute-force sur l'authentification back-office (ADR-0008). En mémoire,
 * suffisant pour un déploiement mono-instance (cf. tech-stack.md).
 */
@Component
public class LoginRateLimiter {

    private final ConcurrentMap<String, Bucket> bucketsParIp = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Bucket> bucketsParIpEtIdentifiant = new ConcurrentHashMap<>();

    public boolean autoriserTentative(String ip, String identifiant) {
        Bucket bucketIp = bucketsParIp.computeIfAbsent(ip, cle -> nouveauBucketIp());
        Bucket bucketCouple = bucketsParIpEtIdentifiant.computeIfAbsent(ip + "|" + identifiant, cle -> nouveauBucketIdentifiant());

        return bucketIp.tryConsume(1) && bucketCouple.tryConsume(1);
    }

    private Bucket nouveauBucketIp() {
        Bandwidth limite = Bandwidth.classic(20, Refill.greedy(20, Duration.ofHours(1)));
        return Bucket.builder().addLimit(limite).build();
    }

    private Bucket nouveauBucketIdentifiant() {
        Bandwidth limite = Bandwidth.classic(5, Refill.greedy(1, Duration.ofMinutes(2)));
        return Bucket.builder().addLimit(limite).build();
    }
}
