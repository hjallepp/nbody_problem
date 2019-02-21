import java.io.IOException;

public class SimSingles {
    public static void main(String[] args) throws IOException, InterruptedException{
        NSQSer.main(args);

        NSQPar.main(args);

        BHSer.main(args);

        BHPar.main(args);
    }
}
