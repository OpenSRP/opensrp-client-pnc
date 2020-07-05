package org.smartregister.pnc.helper;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.util.ReflectionHelpers;

@RunWith(MockitoJUnitRunner.class)
public class PncRulesEngineHelperTest {

    @Test
    public void processDefaultRulesShouldCallRulesEngineFire() {
        PncRulesEngineHelper pncRulesEngineHelper = new PncRulesEngineHelper();
        RulesEngine defaultRulesEngine = Mockito.mock(RulesEngine.class);

        ReflectionHelpers.setField(pncRulesEngineHelper, "defaultRulesEngine", defaultRulesEngine);

        Rules rules = Mockito.mock(Rules.class);
        Facts facts = Mockito.mock(Facts.class);

        pncRulesEngineHelper.processDefaultRules(rules, facts);

        Mockito.verify(defaultRulesEngine, Mockito.times(1))
                .fire(Mockito.eq(rules), Mockito.eq(facts));
    }

    @Test
    public void getRelevanceShouldPerformRelevanceCheck() {
        PncRulesEngineHelper pncRulesEngineHelper = new PncRulesEngineHelper();
        Facts facts = new Facts();
        facts.put("gender", "male");

        Assert.assertTrue(pncRulesEngineHelper.getRelevance(facts, "gender == 'male'"));
    }
}
