package fr.ul.miage;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class CookTest {
    Cook cook = new Cook("test","test","test","test");

    @Test
    //Test pour voir si un menu enfant est prioritaire face a un menu commandé avant celui-ci.
    void ordrePreparationEnfantPrioTest(){
        Preparation preparationEnfant = new Preparation(new ObjectId(),"26-05-2021 13:56:00",false,new ObjectId(),true);
        Preparation preparation = new Preparation(new ObjectId(),"26-05-2021 12:56:00",false,new ObjectId(),false);
        ArrayList<Preparation> preparations = new ArrayList<>();
        preparations.add(preparation);
        preparations.add(preparationEnfant);
        ArrayList<Preparation> orderedpreparations = cook.orderPreparations(preparations);
        Assertions.assertEquals(orderedpreparations.get(0),preparationEnfant);
    }

    @Test
    //Test pour vérifier l'ordre des préparations par rapport aux dates.
    void ordrePreparationDateTest(){
        Preparation preparation = new Preparation(new ObjectId(),"26-05-2021 11:56:00",false,new ObjectId(),false);
        Preparation preparation1 = new Preparation(new ObjectId(),"26-05-2021 09:56:00",false,new ObjectId(),false);
        Preparation preparation2 = new Preparation(new ObjectId(),"27-05-2021 13:56:00",false,new ObjectId(),false);
        Preparation preparation3 = new Preparation(new ObjectId(),"26-07-2021 01:56:00",false,new ObjectId(),false);
        Preparation preparation4 = new Preparation(new ObjectId(),"26-09-2021 12:56:00",false,new ObjectId(),false);
        ArrayList<Preparation> preparations = new ArrayList<>();
        preparations.add(preparation);
        preparations.add(preparation1);
        preparations.add(preparation2);
        preparations.add(preparation3);
        preparations.add(preparation4);
        ArrayList<Preparation> orderedpreparations = cook.orderPreparations(preparations);
        Assertions.assertEquals(orderedpreparations.get(0),preparation1);
    }

}
