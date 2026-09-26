package com.placementor.backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "roadmaps")
public class Roadmap implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String role;

    @Column(name = "roadmap_data", columnDefinition = "TEXT")
    private String roadmapData;

    public Roadmap() {}

    public Roadmap(Long id, Long userId, String role, String roadmapData) {
        this.id = id;
        this.userId = userId;
        this.role = role;
        this.roadmapData = roadmapData;
    }

    public static RoadmapBuilder builder() {
        return new RoadmapBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getRoadmapData() { return roadmapData; }
    public void setRoadmapData(String roadmapData) { this.roadmapData = roadmapData; }

    public static class RoadmapBuilder {
        private Long id;
        private Long userId;
        private String role;
        private String roadmapData;

        public RoadmapBuilder id(Long id) { this.id = id; return this; }
        public RoadmapBuilder userId(Long userId) { this.userId = userId; return this; }
        public RoadmapBuilder role(String role) { this.role = role; return this; }
        public RoadmapBuilder roadmapData(String roadmapData) { this.roadmapData = roadmapData; return this; }

        public Roadmap build() {
            return new Roadmap(id, userId, role, roadmapData);
        }
    }
}
