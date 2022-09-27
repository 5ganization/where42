package openproject.where42.members.domain;

import openproject.where42.groups.domain.Groups;
import openproject.where42.members.domain.enums.Cluster;
import openproject.where42.members.domain.enums.Floor;
import openproject.where42.members.domain.enums.Locate;
import openproject.where42.members.domain.enums.MemberLevel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class Administrators extends Users{
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "owner")
    List<Groups> groups = new ArrayList<>();

    private String msg;

    @Enumerated
    private MemberLevel level = MemberLevel.administrator;

    @Enumerated
    private Cluster cluster;

    @Enumerated
    private Floor floor;

    @Enumerated
    private Locate locate;
}
