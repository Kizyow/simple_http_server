import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class AddressChecker {

    /**
     * Permet de savoir si une adresse IP est égale à une adresse IP donnée avec son masque
     * @param currentAddress L'adresse IP courante
     * @param needAddress L'adresse IP qui doit correspondre
     * @return true si les deux adresses IP correspondent (avec le masque), false sinon
     */
    public static boolean match(InetAddress currentAddress, String needAddress) {

        if (needAddress == null || needAddress.isEmpty() || needAddress.isBlank()) {
            return false;
        }

        byte[] currentAddressArray = currentAddress.getAddress();
        int[] mask = new int[4];

        String[] needAddressSplit = needAddress.split("/");
        int maskDecimal = Integer.parseInt(needAddressSplit[1]);

        for (int i = 0; i < 4; i++) {

            String maskByte = "";

            for (int j = 0; j < 8; j++) {

                if (maskDecimal > 0) {
                    maskByte += "1";
                } else {
                    maskByte += "0";
                }

                maskDecimal--;

            }

            int currentByte = Integer.parseInt(maskByte, 2);
            mask[i] = currentByte;

        }

        byte[] finalAddressArray = new byte[]{
                (byte) (currentAddressArray[0] & mask[0]),
                (byte) (currentAddressArray[1] & mask[1]),
                (byte) (currentAddressArray[2] & mask[2]),
                (byte) (currentAddressArray[3] & mask[3])
        };

        try {

            InetAddress needAddressInet = InetAddress.getByName(needAddressSplit[0]);
            return Arrays.equals(finalAddressArray, needAddressInet.getAddress());

        } catch (UnknownHostException e) {
            System.err.println("Erreur d'hôte inconnu car l'IP est mal écrite, vérifiez votre config.xml");
        }

        return false;

    }

}
