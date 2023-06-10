package main.utilities;

import org.springframework.stereotype.Component;

@Component
public class HexUtils {

    public void printBytesSeparatedWithSpacesGroupedBy16(String hex){
        int byteCounter = 0;
        System.out.println("%%% Hex start %%%");
        for (int i=0; i<hex.length(); i++){
            System.out.print(hex.charAt(i));
            if (i%2==1){
                System.out.print(" ");
            }
            byteCounter++;
            if (byteCounter == 32){
                byteCounter =0;
                System.out.println();
            }
        }
        if (byteCounter !=0){
            System.out.println();
        }
        System.out.println("%%% Hex end %%%");

    }

}
