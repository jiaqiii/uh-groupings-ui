package edu.hawaii.its.holiday.api;

import edu.hawaii.its.holiday.api.type.Group;
import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.internet2.middleware.grouperClient.ws.beans.WsGetAttributeAssignmentsResults;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
public class TestGroupingsService {

    private final static String grouping = "hawaii.edu:custom:test:zknoebel:groupings-api-test";
    private String[] tst = new String[6];
    private String[] tstName = {"tst01fname", "tst02name", "tst03name", "tst04name", "tst05name", "tst06name"};

    @Autowired
    GroupingsService gs;

    @Autowired
    public Environment env; // Just for the settings check.

    @PostConstruct
    public void init() {
        Assert.hasLength(env.getProperty("grouperClient.webService.url"),
                "property 'grouperClient.webService.url' is required");
        Assert.hasLength(env.getProperty("grouperClient.webService.login"),
                "property 'grouperClient.webService.login' is required");
        Assert.hasLength(env.getProperty("grouperClient.webService.password"),
                "property 'grouperClient.webService.password' is required");
    }

    @Before
    public void setUp() {
        for (int i = 0; i < 6; i++) {
            tst[i] = "iamtst0" + (i + 1);
        }
    }

    @Test
    public void isOwnerTest() {
        assertTrue(gs.isOwner(grouping, tst[0]));
    }

    @Test
    public void inGroupTest() {
        assertTrue(gs.inGroup(grouping + ":include", "iamTst01"));
        assertFalse(gs.inGroup(grouping + ":exclude", "iamTst01"));
    }

    @Test
    public void groupOptInPermissionTest() {
        assertTrue(gs.groupOptInPermission(tst[1], grouping + ":include"));
        assertTrue(gs.groupOptInPermission(tst[1], grouping + ":exclude"));
    }

    @Test
    public void groupOptOutPermissionTest() {
        assertTrue(gs.groupOptOutPermission(tst[1], grouping + ":include"));
        assertTrue(gs.groupOptOutPermission(tst[1], grouping + ":exclude"));
    }

    @Test
    public void updateLastModifiedTest() {
        // Test is accurate to the minute, and if checks to see if the current
        // time gets added to the lastModified attribute of a group if the
        // minute happens to change in between getting the time and setting
        // the time, the test will fail.

        String group = grouping + ":include";
        String currentDateTime = gs.wsDateTime();

        gs.updateLastModified(group);

        String assignType = "group";
        String nameName = "uh-settings:attributes:for-groups:last-modified:yyyymmddThhmm";

        WsGetAttributeAssignmentsResults assignments =
                gs.attributeAssignmentsResults(assignType, group, nameName);
        String assignedValue = assignments.getWsAttributeAssigns()[0].getWsAttributeAssignValues()[0].getValueSystem();
        assertEquals(currentDateTime, assignedValue);
    }

    @Test
    public void getOwnersTest(){
        Group owners = gs.getOwners(grouping, tst[0]);

        assertTrue(owners.getUsernames().contains(tst[0]));
        assertFalse(owners.getUsernames().contains(tst[1]));
    }


    @Test
    public void optOutPermissionTest(){
        assertTrue(gs.optOutPermission(tst[0], grouping));
    }


    @Test
    public void optInPermissionTest(){
        assertTrue(gs.optInPermission(tst[0], grouping));
    }

    @Test
    public void hasListServeTest(){
        assertTrue(gs.hasListServe(grouping));
    }

    
    //TODO add test for groupingsIn
    //TODO add test for groupingsOwned
    //TODO add test for groupingsToOptOutOf
    //TODO add test for groupingsToOptInto
    //TODO add test for addSelfOpted
    //TODO add test for checkSelfOpted
    //TODO add test for inGroup
    //TODO add test for isOwner
    //TODO add test for removeSelfOpted
    //TODO add test for wsDateTime
    //TODO add test for groupOptOutPermission
    //TODO add test for groupOptInPermission
    //TODO add test for updateLastModified
    //TODO add test for makeWsSubject
    //TODO add test for makeWsGroupLookup
    //TODO add test for assignAttributesResults (both)
    //TODO add test for attributeAssign
    //TODO add test for attributeAssignments
    //TODO add test for grouperPrivilegesLite (both)
    //TODO add test for membershipsResults
    //TODO add test for addMemberAs
    //TODO add test for deleteMemberAs
    //TODO add test for getMember
    //TODO add test for allGroupings
    //TODO add test for extractGroupings
    //TODO add test for getGroupNames
    //TODO add test for makeGroupings
    //TODO add test for extractGroupNames
    //TODO add test for extractGroupingNames
    //TODO add test for removeGroupOwnership
    //TODO add test for addGroupOwnership
    //TODO add test for makeGroup
    //TODO add test for makePerson
    //TODO add test for groupingNamesFromPrivilegeResults
}
