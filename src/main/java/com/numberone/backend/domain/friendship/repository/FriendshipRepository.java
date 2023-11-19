package com.numberone.backend.domain.friendship.repository;

import com.numberone.backend.domain.friendship.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
}
