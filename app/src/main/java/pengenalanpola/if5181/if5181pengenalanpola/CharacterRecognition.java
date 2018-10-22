package pengenalanpola.if5181.if5181pengenalanpola;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import static android.content.ContentValues.TAG;

public class CharacterRecognition {

    public  Classifier Classifier;

    List tf = new ArrayList(Arrays.asList("True","False")
    );

    public final  Attribute endpoints = new Attribute("endpoints");
    public final  Attribute ep0 = new Attribute("ep0");
    public final  Attribute ep1 = new Attribute("ep1");
    public final  Attribute ep2 = new Attribute("ep2");
    public final  Attribute ep3 = new Attribute("ep3");
    public final  Attribute ep4 = new Attribute("ep4");
    public final  Attribute ep5 = new Attribute("ep5");
    public final  Attribute ep6 = new Attribute("ep6");
    public final  Attribute ep7 = new Attribute("ep7");
    public final  Attribute hTop = new Attribute("hTop", tf);
    public final  Attribute hMid = new Attribute("hMid", tf);
    public final  Attribute hBottom = new Attribute("hBottom", tf);
    public  final Attribute vLeft = new Attribute("vLeft", tf);
    public  final Attribute vMid = new Attribute("vMid", tf);
    public final  Attribute vRight = new Attribute("vRight", tf);
    public final  Attribute lTop = new Attribute("lTop", tf);
    public final  Attribute lMid = new Attribute("lMid", tf);
    public final  Attribute lBottom = new Attribute("lBottom", tf);

    public  final List<String> classes = new ArrayList<String>(
            Arrays.asList("A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P",
                    "Q","R","S","T","U","V","W","X","Y","Z","a","b","c","d","e","f","g","h","i","j",
                    "k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","0","1",
                    "2","3","4","6","8","9","5","7")
    );

    public  Instances dataUnpredicted;



    public CharacterRecognition() {

        try {
            Classifier = (Classifier) SerializationHelper.
                    read(new FileInputStream("/storage/emulated/0/Documents/NBUChar.model"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // Weka "catch'em all!"
            e.printStackTrace();
        }

        ArrayList<Attribute> attributeList = new ArrayList<Attribute>(2) {
            {
                add(endpoints);
                add(ep0);
                add(ep1);
                add(ep2);
                add(ep3);
                add(ep4);
                add(ep5);
                add(ep6);
                add(ep7);
                add(hTop);
                add(hMid);
                add(hBottom);
                add(vLeft);
                add(vMid);
                add(vRight);
                add(lTop);
                add(lMid);
                add(lBottom);

                Attribute attributeClass = new Attribute("@@class@@", classes);
                add(attributeClass);
            }
    };

        dataUnpredicted = new Instances("TestInstances",
                attributeList, 1);

        int idxClass = dataUnpredicted.numAttributes() - 1;
        dataUnpredicted.setClassIndex(idxClass);

//        Classifier = (NaiveBayes) SerializationHelper.
//        read(new FileInputStream("/Users/dayatura/Documents/Master\\ of\\ Informatics\\ \\(ITB\\)/Pattern\\ Recognition/naiveBayesKarakter.model"));
    }

    private  String BooleanTranslator(Boolean bool){
        if(bool) return "True";
        else return "False";
    }

    public  String predict(SkeletonFeature fiture) {

        final SkeletonFeature fitur = fiture;


        DenseInstance newInstance = new DenseInstance(dataUnpredicted.numAttributes()) {
            {
                setValue(endpoints, fitur.endpoints.size());
                setValue(ep0,       fitur.epHeading[0]      );
                setValue(ep1,       fitur.epHeading[1]      );
                setValue(ep2,       fitur.epHeading[2]      );
                setValue(ep3,       fitur.epHeading[3]      );
                setValue(ep4,       fitur.epHeading[4]      );
                setValue(ep5,       fitur.epHeading[5]      );
                setValue(ep6,       fitur.epHeading[6]      );
                setValue(ep7,       fitur.epHeading[7]      );
                setValue(hTop,      BooleanTranslator(fitur.hTop     ));
                setValue(hMid,      BooleanTranslator(fitur.hMid     ));
                setValue(hBottom,   BooleanTranslator(fitur.hBottom  ));
                setValue(vLeft,     BooleanTranslator(fitur.vLeft    ));
                setValue(vMid,      BooleanTranslator(fitur.vMid     ));
                setValue(vRight,    BooleanTranslator(fitur.vRight   ));
                setValue(lTop,      BooleanTranslator(fitur.lTop     ));
                setValue(lMid,      BooleanTranslator(fitur.lMid     ));
                setValue(lBottom,   BooleanTranslator(fitur.lBottom  ));
            }
        };

        newInstance.setDataset(dataUnpredicted);

        String className;
        double result;
        try {
            result = Classifier.classifyInstance(newInstance);
            String hasil = "Hasil result " + result;
            Log.d(TAG, hasil);
            className = classes.get(new Double(result).intValue());
        }catch (Exception e){
            e.printStackTrace();
            className = "err";
        }

        return className;
    }

    public String predicts(SkeletonFeature sf){
        String prediction = null;
        switch (sf.endpoints.size()){
            case 0:
                if (sf.vLeft){
                    if (sf.lMid)prediction = "D";
                    else prediction = "B";
                } else if (sf.lMid) prediction = "O";
                else prediction = "8";
                break;
            case 1:
                if (sf.epHeading[1]==1) prediction = "e";
                else if (sf.hTop) prediction = "P";
                else if (sf.vRight) prediction = "g";
                else if (sf.lTop) prediction = "9";
                else if (sf.lBottom) prediction = "6";
                break;
            case 2:
                if (sf.hTop){
                    if(sf.hMid) prediction = "R";
                    else if (sf.hBottom) prediction = "Z";
                    else prediction = "7";
                }else if (sf.vLeft){
                    if (sf.vRight) prediction = "U";
                    else if (sf.lTop) prediction = "P";
                    else if (sf.lBottom) prediction = "b";
                    else prediction = "L";
                }else if (sf.lMid) prediction = "Q";
                else if (sf.hBottom){
                    if (sf.vRight) prediction = "j";
                    else if (sf.lTop) prediction = "A";
                    else prediction = "2";
                }else if (sf.epHeading[0] == 2) prediction = "J";
                else if (sf.epHeading[1] == 1) prediction = "V";
                else if (sf.epHeading[4] == 2) prediction = "a";
                else if (sf.lTop) prediction = "q";
                else if (sf.lBottom) prediction = "d";
                else if (sf.epHeading[6] == 1) prediction = "G";
                else if (sf.epHeading[4] == 0) prediction = "s";
                else prediction = "I";
                break;
            case 3:
                if (sf.vMid) prediction = "T";
                else if (sf.epHeading[2] == 3) prediction = "E";
                else if (sf.hTop){
                    if (sf.vLeft) prediction = "F";
                    else prediction = "5";
                }else if (sf.epHeading[0] == 2){
                    if (sf.epHeading[3] == 1) prediction = "v";
                    else prediction = "u";
                }else if(sf.hBottom) prediction = "4";
                else if (sf.vLeft){
                    if (!sf.vRight) prediction = "r";
                    else if (sf.epHeading[7]==1) prediction = "n";
                    else prediction = "h";
                }else if (sf.vRight) prediction = "1";
                else if (sf.epHeading[7]==0) prediction = "3";
                else if (sf.epHeading[6]==0) prediction = "Y";
                else prediction = "y";
                break;
            case 4:
                if (sf.hMid) prediction = "H";
                else if (sf.vMid){
                    if (sf.vRight) prediction = "m";
                    else prediction = "t";
                }
                else if (sf.hBottom) prediction = "z";
                else if (sf.vRight) prediction = "N";
                else if (sf.hTop) prediction = "f";
                else if (sf.epHeading[0] == 0){
                    if (sf.epHeading[4] == 0)prediction = "x";
                    else prediction = "k";
                }
                else {
                    if (sf.epHeading[4] == 0)prediction = "X";
                    else prediction = "K";
                }
                break;
            case 5:
                if(sf.epHeading[4] == 3) prediction = "M";
                else prediction = "W";
                break;
            default:
                prediction = "unknown";
        }
        return prediction;
    }
    
}
