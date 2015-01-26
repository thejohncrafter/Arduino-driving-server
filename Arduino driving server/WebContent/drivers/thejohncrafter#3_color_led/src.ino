
const int red    = 9;  // le pin du rouge
const int blue   = 10; // le pin du bleu
const int green  = 11; // le pin du vert

const int waitLed = 2; // cette led est allumée lorsque l'arduino effectue une action
const int okLed   = 3; // cette led est allumée lorsque l'arduino est disponible

int redValue   = 0; // la valeur du rouge
int blueValue  = 0; // la valeur du bleu
int greenValue = 0; // la valeur du vert

int prevRed   = 0; // la précédente valeur du rouge (utilisée pour les fondus)
int prevBlue  = 0; // la précédente valeur du bleu (utilisée pour les fondus)
int prevGreen = 0; // la précédente valeur du vert (utilisée pour les fondus)

void fade (int from, int to, int port){
  
  if(from > to){ // fondu descendant
    
    for(; from >= to; from--){ // on utilise from
      
      analogWrite(port, from);
      delay(10);
      
    }
    
  }else{ // fondu montant
    
    for(; from <= to; from++){ // on utilise from
      
      analogWrite(port, from);
      delay(10);
      
    }
    
  }
  
}

void setup() {
  
  // on initialise les pin des leds en tant que sorties
  pinMode(red, OUTPUT);
  pinMode(blue, OUTPUT);
  pinMode(green, OUTPUT);
  
  pinMode(okLed, OUTPUT);
  pinMode(waitLed, OUTPUT);
  
  // on initialise l'usb
  Serial.begin(9600);
  
  digitalWrite(okLed, HIGH);
  
}

void loop() {
  
  if(Serial.available() > 0){ // on a une requête
    
    digitalWrite(okLed, LOW);    // l'arduino effectue une ation
    digitalWrite(waitLed, HIGH); // on demande à l'utilisateur d'attendre
    
    String request = Serial.readString(); // on récupère la requête
    String type = request.substring(0, 3); // on récupère le type de requête (les 3 premuers caractères)
    String answ = "null";
    
    /*
    * Commandes valables :
    * HID - retourne l'HID (HardwareIDentifier) du hardware
    * SET - permet de mettrea à jour les couleurs
    * GET - permet de connaître les valeurs des leds
    * END - éteint le hardware
    */
    if(type == "HID"){ // on demande l' HID (HardwareIDentifier)
      
      answ = "thejohncrafter#3 color led";
      
    }else if(type == "GET"){ // demande des valeurs de la lumière
      
      answ = "";
      answ += redValue;
      answ += " ";
      answ += greenValue;
      answ += " ";
      answ += blueValue;
      
    }else if(type == "SET"){ // demande de mise à jour des valeurs
      
      // on met à jour les valeurs des couleurs
      redValue   = atoi(request.substring(4, 7).c_str());
      greenValue = atoi(request.substring(8, 11).c_str());
      blueValue  = atoi(request.substring(12, 15).c_str());
      
      if(redValue == NULL)
        redValue = 0;
      
      if(greenValue == NULL)
        greenValue = 0;
      
      if(blueValue == NULL)
        blueValue = 0;
      
      // on fait les fondus pour un rendu plus propre
      fade(prevRed, redValue, red);
      fade(prevGreen, greenValue, green);
      fade(prevBlue, blueValue, blue);
      
      // on reset les valeurs de prevRed, prevGreen et prevBlue pour les prochains fondus
      prevRed = redValue;
      prevGreen = greenValue;
      prevBlue = blueValue;
      
      answ = "SUCCESSFULL";
      
    }else if (type == "END"){ // demande d'arrêt
      
      // on arrête le programme pour débrancher "proprement" le hardware
      
      // on fait les fondus pour éteindre les led
      fade(prevRed, 0, red);
      fade(prevGreen, 0, green);
      fade(prevBlue, 0, blue);
      
      digitalWrite(waitLed, LOW); // on éteint les dernières leds
      digitalWrite(okLed, LOW);
      
      Serial.println("END"); // on informe l'ordinateur de l'extinction du hardware
      
      delay(1000); // pour terminer d'écrire
      
      exit(0);
      
    }else{ // aucune requête correspondante
      
      answ = "NOSUCH : " + type; // on informe l'ordinateur
      
    }
    
    digitalWrite(waitLed, LOW); // plus besoin d'attendre
    digitalWrite(okLed, HIGH);  // l'arduino est prête
    
    Serial.println(answ); // On renvoie la réponse
    
  }
  
}

