package chapter2;

/**
 * 数据竞争
 *
 * @author neal.ma
 * @date 2019/6/17
 * @blog nealma.com
 */
public class DataRace {

    public static void main(String[] args) {
        // todo
    }
}

class Parser {

    private static Parser parser;

    public static Parser getInstance(){
        if( parser == null ){
            parser = new Parser();
        }
        return parser;
    }
}