package cc.xylitol.module.impl.misc;

import cc.xylitol.event.annotations.EventTarget;
import cc.xylitol.event.impl.events.EventAttack;
import cc.xylitol.event.impl.events.EventUpdate;
import cc.xylitol.event.impl.events.EventWorldLoad;
import cc.xylitol.module.Category;
import cc.xylitol.module.Module;
import cc.xylitol.utils.TimerUtil;
import cc.xylitol.value.impl.ModeValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.RandomUtils;

public class KillInsults extends Module {

    public KillInsults() {
        super("KillInsults", Category.Misc);
    }

    private ModeValue mode = new ModeValue("Mode", new String[]{"Random", "Anjian Chen", "Silence", "Japan", "Mao Niang", "Ci Xiao Gui", "Ge ge","illness"}, "Random");

    private final String CLIENT = "asaka" + "\u02cc" + "lol";

    private TimerUtil timer = new TimerUtil();
    private final String[] INSULTS = {
            "Better config? No, better client - " + CLIENT,
            "My aura obviously shows superiority to %s's!",
            "How did %s even press start without Xylitol?",
            "Get Xylitol at " + CLIENT,
            "%s I swear I'm not cheating!",
            "You have just encountered a death by " + CLIENT + "!",
            "Stop being a bow spamming freak %s",
            "Insane1337 crashed the mercedes amg into %s",
            "%s got shit on by " + CLIENT,
            "Xylitol to winning with " + CLIENT + "!",
            "Stop jerking off and get " + CLIENT,
            "%s, Do you know what a Liquidbounce is? I sure don't",
            "What's a Liquidbounce?",
            "Wait %s, Do you know what a GrimAC is? I'm trying to find him!",
            "GrimAC x LiquidBounce",
            "You've gyat to get Xylitol at " + CLIENT,
            "I'm currently playing with one hand and jerking off with the other - only at" + CLIENT
    };


    private final String[] CHEN_INSULTS = {
            "这%s咋处理，我必须得害死他了",
            "我被%s害死了",
            "%s 比额数是吧 那你必输 看看吧",
            "%s 花Y庭我服 我想咋改咋改",
            "%s 你被陈安键害死了，使用Trap客H端确保陈安键不会害你。",
            "%s 陈安键之Hack Lunar客H端发布了，快来够麦。",
            "%s 类加密就叫壳",
            "%s 事成了我肯定得去看你 看看到时候还这么魔怔不",
            "我跟%s本深仇大恨，我得看他咋似的",
            "我成功害死了%s，哥们，赫赫一切都在我的计算之中",
            "%s 现在我只能退网了啊 哥们",
            "? 咋了 %s裂不了 %s也裂不开",
            "%s ? 这个抱紧能处理吗",
            "我赚的前 没%s零头大",
            "WC 怎么都有%s呼机了",
            "%s估计要裂他端 完全底层",
            "得想个办法让%s额数我啊 我好抱紧啊 把那几个仇视我的直接告发源了",
            "%s [语音通话]未接听，点击回拨 别闹 好好说 哥们 你是不是之前开的"
    };


    private final String[] XU_INSULTS = {
            "下次再魔怔%s 晚安",
            "这客户端啊，我有三不用  %s被管理员禁言30天",
            "而且 我不怕%s 他的人脉 搞不了我 知道不",
            "别啊 那%s这样的话 我就不能骂你老安卓了",
            "这个服不让%s这样的臭蛆睡觉 栓到犬笼子 不让睡",
            "%s比较喜欢抄袭Silence",
            "你只是比不过%s而已 你有人脉 只是比不过%s",
            "%s这老安卓 我也是服了",
            "%s用老安卓，读老职高。",
            "%s没用老安卓",
            "%s你好，我是许锦良1337，我正在使用老安卓。",
            "%s 你被金牙牌老安卓骂死了，获qu老安卓或对线278023413。",
            "%s使用他的老安卓和许锦良的漏电黑解14pm扣字。",
            "%s 內步是60圆哦亲，您给了欣欣40圆，请再给我20圆呢",
            "您需要之父90圆鸭金呢亲，因为4.5被老安卓泄露了呢",
            "验证费20哦亲",
            "不是吧,%s不会还在用这老安卓吧",
            "不是吧,%s余额连1w都没有",
            "你被老安卓客H端击杀!",
            "%s，你又在用老安卓了吗？",
            "别啊，%s，你老安卓还没换吗？",
            "我听说%s最近在用老安卓，是吗？",
            "哎呀，%s还在坚持用老安卓啊。",
            "不是吧，%s还在用那个老安卓版本？",
            "听说%s还在用老安卓系统，真是佩服。",
            "别这样啦，%s用老安卓也没什么不好嘛。",
            "我想知道%s为什么还在坚持用老安卓系统。",
            "哇，%s用老安卓的勇气真值得尊敬。",
            "不是吧，%s居然还在用老安卓系统？"
    };

    private final String[] Meow2 = {
            "对不起%s，我又调皮了呢，喵~",
            "主人%s，是我的不对，我任你摆布呢，喵~",
            "喵喵喵%s，主人你怎么这么晚才回来呢，喵？",
            "主人%s，你去哪了呀，我等了好久呢，喵~",
            "抱歉%s，我刚才玩得太high了，喵~",
            "主人%s，不要生气，我会改的，喵~",
            "主人%s，你的袜子好好闻，我想玩一下，可以吗，喵？",
            "喵喵%s，今天我跟着你出门吧，喵~",
            "对不起%s，我又不小心把东西弄倒了，喵~",
            "主人%s，我饿了，可以给我点零食吗，喵？",
            "喵喵%s，我刚才睡着了，对不起，喵~",
            "对不起%s，我跑出去玩了一会儿，喵~",
            "主人%s，你的衣服好好玩，我要玩，喵~",
            "喵喵喵%s，今天天气好适合晒太阳，喵~",
            "对不起%s，我刚才又抓坏了沙发，喵~",
            "主人%s，你看我新学的技能，喵~",
            "喵喵%s，你能陪我玩一会儿吗，喵？",
            "对不起%s，我刚才又把你的鞋子藏起来了，喵~",
            "主人%s，你刚才在干嘛呢，我在找你，喵~",
            "喵喵喵%s，我能坐在你身上吗，喵？",
            "对不起%s，我刚才又踩碎了花盆，喵~",
            "主人%s，我刚才在玩得太high了，把你的书弄乱了，对不起，喵~",
            "喵喵%s，你看我刚才捉到的玩具，好开心呀，喵~",
            "对不起%s，我刚才在窗台上乱蹦乱跳，把花瓶打翻了，喵~",
            "主人%s，我想和你一起看电视，可以坐在你旁边吗，喵？",
            "喵喵%s，我刚才睡着了，做了个好梦，喵~",
            "对不起%s，我又跑到了你的床上打滚了，弄乱了被子，喵~",
            "主人%s，你看我刚才抓到的虫子，好好玩呀，喵~",
            "喵喵喵%s，我想跟你一起看夕阳，可以吗，喵？",
            "对不起%s，我刚才在厨房调皮，把垃圾撕破了，喵~"
    };

    private final String[] cixiaogui1 = {
            "嘻嘻%s你这点小事就要认输了吗？",
            "嘻嘻%s你还真是个杂鱼呢！",
            "杂鱼%s杂鱼杂鱼杂鱼杂鱼！",
            "杂鱼%s行不行?",
            "杂鱼%s你这样真的可以吗？我都要为你感到害羞了呢。",
            "呐杂鱼%s你这么容易被捉弄，是不是该练练了？",
            "杂鱼%s你怎么动不动就脸红了嘻嘻",
            "杂鱼%s你这点小伤就受不了了？真是个杂鱼呢！",
            "杂鱼%s你要是再笨一点，我都要当你是fvv了。",
            "嘻嘻杂鱼%s你的问题真是百思不得其解啊。",
            "杂鱼%s你是不是想难住我？可惜我是雌小鬼啊。",
            "杂鱼%s你这么容易就生气了？我还以为你更坚强呢。",
            "呐杂鱼%s你这么紧张干嘛？我又不会真的抱你回家。",
            "杂鱼%s你这点小挫折就开始心灰意冷了？太没劲了吧。",
            "嘻嘻杂鱼%s你的反应也太慢了吧，是不是该练练脑子？",
            "杂鱼%s你是不是想让我给你点安慰呢？可惜我不会啊。",
            "杂鱼%s你这点小疼痛就瑟瑟发抖了？有点夸张吧。",
            "呐杂鱼%s你这么容易被摆布，是不是该反省一下自己？",
            "杂鱼%s你这样真的好吗？我都要为你感到尴尬了呢。",
            "嘻嘻杂鱼%s你这么容易被捉弄，是不是该变得聪明一点？"
    };

    private final String[] gege1 = {
            "%s，你是不是喜欢她",
            "我喜欢你~，%s♥",
            "我喜欢你~，很久很久%s♥",
            "和我交往吧~%s♥",
            "(呜~%s，的衣服好好闻啊?），%s不是你想的那样。",
            "(%s，怎么带一个女的回家了??难的...难的是%s的女朋友?)",
            "好喜欢成熟，%s~",

    };
    private final String[] Japan1 = {
            "%s わ♥た♥し♥は♥あ♥な♥た♥が♥好♥き♥で♥す",
            "%s 私と一緒にいられますか?",
            "%s 好き～",
            "はは %s 好き～",

    };

    private final String[] sicks = {
            "%s有嘴臭，建议别说话。",
            "%s有脚臭，建议别拖鞋。",
            "%s有流感，需要休息，饮食清淡，喝足水。",
            "%s有感冒，需要休息，多喝水，服用退热镇痛药。",
            "%s有癌症，需要手术，化疗，放疗。",
            "%s有糖尿病，需要控制饮食，运动，药物治疗。",
            "%s有高血压，需要饮食调理，锻炼，药物治疗。",
            "%s有心脏病，需要改变生活方式，药物治疗，可能需要手术。",
            "%s有中风，需要急救护理，康复训练，药物治疗。",
            "%s有高血脂，需要低脂饮食，运动，药物治疗。",
            "%s有FEl炎，需要抗生素治疗，休息，补充水分。",
            "%s有非酒精性脂肪性肝病，需要控制体重，改变饮食，避免饮酒。",
            "%s有慢性阻塞性肺病，需要吸氧治疗，药物治疗，康复锻炼。",
            "%s有哮喘，需要吸入支气管舒张剂，避免诱发因素，持续控制治疗。",
            "%s有慢性肾脏病，需要控制高血压，血糖，饮食调理。",
            "%s有类风湿性关节炎，需要药物治疗，物理治疗，手术。",
            "%s有帕金森病，需要药物治疗，物理治疗，手术。",
            "%s有痛风，需要药物治疗，饮食调理，控制体重。",
            "%s有过敏性鼻炎，需要避免过敏原，抗组胺药物，鼻腔冲洗。",
            "%s有贫血，需要补充铁剂，补充维生素，改善饮食。",
            "%s有糖尿病性视网膜病变，需要控制血糖，眼科治疗。",
            "%s有慢性胃炎，需要药物治疗，饮食调理，避免刺激性食物。",
            "%s有肝炎，需要抗病毒药物，休息，饮食调理。",
            "%s有食管癌，需要手术，放疗，化疗。",
            "%s有结肠癌，需要手术，放疗，化疗。",
            "%s有胃癌，需要手术，放疗，化疗。",
            "%s有鼻咽癌，需要手术，放疗，化疗。",
            "%s有肺癌，需要手术，放疗，化疗。",
            "%s有乳腺癌，需要手术，放疗，化疗。",
            "%s有前列腺癌，需要手术，放疗，化疗。",
    };

    private EntityPlayer target;

    @EventTarget
    private void onAttack(EventAttack eventAttack) {
        final Entity entity = eventAttack.getTarget();

        if (entity instanceof EntityPlayer) {
            target = (EntityPlayer) entity;
        }
    }

    ;

    @EventTarget
    private void onUpdate(EventUpdate eventAttack) {
        if (target != null && !mc.theWorld.playerEntities.contains(target)) {
            if (mc.thePlayer.ticksExisted > 20 && !mc.thePlayer.isSpectator() && !mc.thePlayer.isDead) {
                switch (mode.get()) {
                    case "Random":
                        mc.thePlayer.sendChatMessage(INSULTS[RandomUtils.nextInt(0, INSULTS.length)].replaceAll("%s", target.getName()));
                        break;
                    case "Anjian Chen":
                        mc.thePlayer.sendChatMessage(CHEN_INSULTS[RandomUtils.nextInt(0, CHEN_INSULTS.length)].replaceAll("%s", target.getName()));
                        break;
                    case "Silence":
                        mc.thePlayer.sendChatMessage(XU_INSULTS[RandomUtils.nextInt(0, XU_INSULTS.length)].replaceAll("%s", target.getName()));
                        break;
                    case "Mao Niang":
                        mc.thePlayer.sendChatMessage(Meow2[RandomUtils.nextInt(0, Meow2.length)].replaceAll("%s", target.getName()));
                        break;
                    case "Ci Xiao Gui":
                        mc.thePlayer.sendChatMessage(cixiaogui1[RandomUtils.nextInt(0, cixiaogui1.length)].replaceAll("%s", target.getName()));
                        break;
                    case "Ge ge":
                        mc.thePlayer.sendChatMessage(gege1[RandomUtils.nextInt(0, gege1.length)].replaceAll("%s", target.getName()));
                        break;
                    case "Japan":
                        mc.thePlayer.sendChatMessage(Japan1[RandomUtils.nextInt(0, Japan1.length)].replaceAll("%s", target.getName()));
                        break;
                    case "illness":
                        mc.thePlayer.sendChatMessage(sicks[RandomUtils.nextInt(0, sicks.length)].replaceAll("%s", target.getName()));
                        break;
                }
            }
            target = null;
        }


    }

    @EventTarget
    private void onLoadWorld(EventWorldLoad eventWorldLoad) {
        target = null;
    }
}