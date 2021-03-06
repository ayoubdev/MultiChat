package com.adibsarr.multichat.model.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javafx.scene.control.TextField;

public class EmissionMulticastService {
	private MulticastSocket multicastSocket;
	private InetAddress address;
	private int port;
	private TextField saisie;
	private String nickname;
	
	/**
	 * @brief Classe permettant d'envoyer des messages à travers la socket
	 * @param multicastSocket Socket permettant de communiquer dans le cas d'une communication multicast
	 * @param address Addresse Ip ou le client va se connecter
	 * @param port
	 * @throws IOException
	 */
	public EmissionMulticastService(MulticastSocket multicastSocket, InetAddress address, int port){
		//On ne binde pas car seulement envoie de donnée en UDP:
		this.multicastSocket = multicastSocket;
		this.address = address;
		this.port = port;
		//Pseudonyme généré aléatoirement par défaut:
		this.nickname = new String("Guest-"+(int)(Math.random()*(10000 - 1)));
		this.saisie = null;
	}
	
	/**
	 * @brief Envoi du texte saisi par l'utilisateur
	 * @param saisie Texte saisie dans la fenêtre JavaFX
	 * @return true si l'envoi si bien passé, false si il y a un probleme de connexion
	 */
	public boolean updateSaisie(TextField saisie) {
		byte[] dataOut = new byte[1024];
		DatagramPacket paquetOut;
		
		if(saisie != null) {
			this.saisie = saisie;
			paquetOut = new DatagramPacket(dataOut,0,0,this.address,this.port);
			try {
				String message = this.saisie.getText();
				if(message.toLowerCase().contains("/nick ") == true) {
					//Gestion des pseudos:
					//this.nickname = message.replaceAll("/nick(.*?)(\n)","$2");
					this.nickname = message.substring(message.indexOf("/nick ")+"/nick ".length(), message.length());
					System.out.println("Added Nickname: "+this.nickname);
				}
				else {
					message = this.nickname+" said: "+message;
					dataOut = message.getBytes();
					paquetOut.setData(dataOut, 0, dataOut.length);
				    this.multicastSocket.send(paquetOut);
				    
				    /*System.out.println("Sended data from: " + paquetOut.getAddress().toString() +
						    ":" + paquetOut.getPort() + " size: " +
						    paquetOut.getLength()+"\n");*/
				}
			    return true;
			} catch (IOException e) {
			    System.out.println(e);
			}
		}
		
		return false;
	}

	public TextField getSaisie() {
		return saisie;
	}

	public void setSaisie(TextField saisie) {
		this.saisie = saisie;
	}
}
