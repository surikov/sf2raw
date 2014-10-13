/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sf2read;

import nocom.sun.media.sound.*;
import java.io.*;
import java.io.*;
import java.util.*;
import nocom.sun.media.sound.*;

/**
 *
 * @author surikov
 */
public class Sf2read {

    //static double timecent = (Math.log(2.0) / 1200.0);
    public static void main(String[] args) {
	try {
	    export("SYNTHGMS.SF2", "SynthGMS");
	    //export("kamac_distortion_guitar.sf2", "Kamac");
	    //export("1115_Bass_Jam.sf2","BassJam");
	    //export("chorium.sf2");
	    //export("Chaos_V20.sf2","Chaos");
	    //export("Guitar_FX.sf2", "GuitarFX");
	    //export("bennetng_AnotherGS_v2-1.sf2");
	    //export("FluidR3_GM.sf2","FluidR3");
	    //System.out.println(10000*timecent);
	    /*double pitchcorrection = -7100;
	     double pitch = 440;
	     double samplerateconv = 1.0;
	     double target_pitch = (float) Math.exp(
	     (pitchcorrection + pitch) * (Math.log(2.0) / 1200.0))
	     * samplerateconv;
	     System.out.println(target_pitch);*/
	} catch (Throwable t) {
	    t.printStackTrace();
	}
    }

    static void export(String sf2name, String libraryName) throws Exception {
	String folder = "D:\\projects\\soundfont\\extracted\\";
	String sf2path = "D:\\projects\\soundfont\\" + sf2name;
	FileInputStream stream = new FileInputStream(new File(sf2path));
	SF2Soundbank bank = new SF2Soundbank(stream);
	SF2Instrument[] instruments = bank.getInstruments();
	/*for (int i = 0; i < instruments.length; i++) {
	 SF2Instrument instrument = instruments[i];
	 if (instrument.isDrum()) {
	 System.out.println(instrument.preset + ": " + instrument.getName());
	 }
	 }*/
	for (int i = 0; i < instruments.length; i++) {
	    SF2Instrument instrument = instruments[i];
	    if (instrument.preset == 0) {
		//if (!instrument.isDrum()) {
		//System.out.println(instrument.preset+"/"+instrument.bank + ": " + instrument.getName());
		exportInstrument(sf2name, bank, instrument, folder, libraryName);
	    }
	}
    }

    static String pad0(String s) {
	if (s.length() < 3) {
	    s = "0" + s;
	}
	if (s.length() < 3) {
	    s = "0" + s;
	}
	return s;
    }

    static String safe(String s) {
	return s//
		.replace(' ', '-')//
		.replace('#', 'z')//
		.replace('/', '-')//
		.replace('\\', '-')//
		.replace(',', '-')//
		.replace('\'', '-')//
		.replace('"', '-')//
		.replace(':', '-')//
		.replace('(', '-')//
		.replace(')', '-')//
		.replace('<', '-')//
		.replace('>', '-')//
		.replace('|', '-')//
		;
    }

    static void exportInstrument(String sf2name, SF2Soundbank bank, SF2Instrument instrument, String folder, String libraryName) {
	ModelPerformer[] modelPerformers = instrument.getPerformers();
	
	String kind = "instruments";
	if (instrument.isDrum()) {
	    kind = "drums";
	};
	System.out.println(kind + " " + instrument.preset);
	//instrument.preset + "/" + instrument.bank + ": " + instrument.getName());//+" /performers: "+modelPerformers.length);
	for (int performerCount = 0; performerCount < modelPerformers.length; performerCount++) {
	    ModelPerformer modelPerformer = modelPerformers[performerCount];
	    //System.out.println("    " + modelPerformer);
	    /*String fileName = kind + instrument.preset//
	     + "_range" + modelPerformer.getKeyFrom() + "-" + modelPerformer.getKeyTo()//
	     + "_bank" + instrument.bank//
	     ;*/
	    //System.out.println(fileName);
	    System.out.println("\t\t" + performerCount + "/" + modelPerformers.length + " range " + modelPerformer.getKeyFrom() + "-" + modelPerformer.getKeyTo() + " /" + modelPerformer.getName());

	    //System.out.println("performer "+performerCount+": "+modelPerformer.getName());
	    int cntr = 0;
	    boolean found = false;
	    List<SF2InstrumentRegion> instrumentRegions = instrument.getRegions();
	    for (int r = 0; r < instrumentRegions.size(); r++) {
		SF2InstrumentRegion instrumentRegion = instrumentRegions.get(r);
		/*System.out.println(//
			instrumentRegion.getShort(SF2Region.GENERATOR_COARSETUNE)//
			+" / "//
			+instrumentRegion.getShort(SF2Region.GENERATOR_FINETUNE)//
			);*/
		SF2Layer layer = instrumentRegion.getLayer();
		//System.out.println("layer "+r+": "+layer.getName());

		List<SF2LayerRegion> layerRegions = layer.getRegions();

		for (int lare = 0; lare < layerRegions.size(); lare++) {
		    if (cntr == performerCount) {
			SF2LayerRegion layerRegion = layerRegions.get(lare);
			SF2Sample sample = layerRegion.getSample();
			//System.out.println("\t\t" + sample);
			String folderPath = folder//
				+ kind
				+ "\\" + pad0("" + instrument.preset)//
				+ "\\" + safe(libraryName)
				+ "_" + pad0("" + instrument.bank)//
				;
			String fileName = pad0("" + performerCount)//
				+ "_" + pad0("" + modelPerformer.getKeyFrom()) + "-" + pad0("" + modelPerformer.getKeyTo())//
				+ "_" + sample.getOriginalPitch()
				+ "_" + modelPerformer
				+ "_" + sample.getStartLoop() + "-" + sample.getEndLoop()//
				+ "_" + sample.getSampleRate() //
				//+ "_n_" + safe(instrument.getName())//
				//+ "_" + safe(modelPerformer.getName())//
				//+ "_" + safe(sample.getName())//
				;
			//System.out.println(folderPath + ": " + fileName);
			writeSample(folderPath, fileName, sample);
			found = true;
			break;
		    }
		    cntr++;
		}
		if (found) {
		    break;
		}
	    }
	    if (!found) {
		System.out.println("\t\t ---------------------------");
	    }
	}
	/*
	 List<SF2InstrumentRegion> instrumentRegions = instrument.getRegions();
	 for (int r = 0; r < instrumentRegions.size(); r++) {
	 SF2InstrumentRegion instrumentRegion = instrumentRegions.get(r);
	 SF2Layer layer = instrumentRegion.getLayer();
	 System.out.println("\t" + layer);
	 List<SF2LayerRegion> layerRegions = layer.getRegions();
	 for (int ll = 0; ll < layerRegions.size(); ll++) {
	 SF2LayerRegion layerRegion = layerRegions.get(ll);
	 SF2Sample sample = layerRegion.getSample();
	 System.out.println("\t\t" + sample);
	 }
	 }
	 */
    }

    static void writeSample(String path, String name, SF2Sample sample) {
	System.out.println("	write " + path + ": " + name);
	try {
	    new File(path).mkdirs();
	    FileOutputStream fos = new FileOutputStream(path + "\\" + name);
	    BufferedOutputStream b = new BufferedOutputStream(fos);
	    int n1;
	    int n2;
	    int n;
	    for (int i = 0; i < sample.getDataBuffer().capacity(); i = i + 2) {
		n1 = sample.getDataBuffer().array()[(int) sample.getDataBuffer().arrayOffset() + i];
		n2 = sample.getDataBuffer().array()[(int) sample.getDataBuffer().arrayOffset() + i + 1];
		n = ((n2 * 256) + n1) / 256;

		b.write(n);
	    }
	    b.flush();
	    fos.flush();
	    b.close();
	    fos.close();
	} catch (Throwable t) {
	    t.printStackTrace();
	}
    }

    static void exportSample(SF2Instrument instrument, int num) {
	try {
	    List<SF2InstrumentRegion> instrumentRegions = instrument.getRegions();
	    SF2InstrumentRegion instrumentRegion = instrumentRegions.get(0);
	    SF2Layer layer = instrumentRegion.getLayer();

	    SF2LayerRegion layerRegion = layer.getRegions().get(num);
	    SF2Sample sample = layerRegion.getSample();
	    System.out.println("\t\t" + sample.toString());
	} catch (Throwable t) {
	    System.out.println("\t\t-- ops " + t.getMessage());
	}
    }

    /**
     * @param args the command line arguments
     */
    public static void _main(String[] args) {
	System.out.println("start");
	//String sf2 = "D:\\projects\\soundfont\\SYNTHGMS.SF2";
	//String sf2 = "D:\\projects\\soundfont\\kamac_distortion_guitar.sf2";
	//String sf2 = "D:\\projects\\soundfont\\chorium.sf2";
	String sf2 = "D:\\projects\\soundfont\\FluidR3_GM.sf2";
	try {

	    FileInputStream stream = new FileInputStream(new File(sf2));
	    SF2Soundbank bank = new SF2Soundbank(stream);
	    System.out.println("getCreationDate " + bank.getCreationDate());
	    System.out.println("getDescription " + bank.getDescription());
	    System.out.println("getName " + bank.getName());
	    System.out.println("getProduct " + bank.getProduct());
	    System.out.println("getRomName " + bank.getRomName());
	    System.out.println("getRomVersionMajor " + bank.getRomVersionMajor());
	    System.out.println("getRomVersionMinor " + bank.getRomVersionMinor());
	    System.out.println("getTargetEngine " + bank.getTargetEngine());
	    System.out.println("getTools " + bank.getTools());
	    System.out.println("getVendor " + bank.getVendor());
	    System.out.println("getVersion " + bank.getVersion());

	    SF2Instrument[] instruments = bank.getInstruments();
	    System.out.println("instruments");
	    for (int i = 0; i < instruments.length; i++) {
		SF2Instrument instrument = instruments[i];
		System.out.println(instrument);
		ModelPerformer[] ps = instrument.getPerformers();
		for (int p = 0; p < ps.length; p++) {
		    ModelPerformer modelPerformer = ps[p];
		    System.out.println("\tname " + modelPerformer.getName()//
			    + ", range " + modelPerformer.getKeyFrom() + "-" + modelPerformer.getKeyTo()//
			    );
		    List<ModelOscillator> oscillators = modelPerformer.getOscillators();
		    for (int o = 0; o < oscillators.size(); o++) {
			ModelOscillator oscillator = oscillators.get(o);
			if (oscillator instanceof ModelByteBufferWavetable) {
			    ModelByteBufferWavetable modelByteBufferWavetable = (ModelByteBufferWavetable) oscillator;
			    System.out.println("\t\t" + modelByteBufferWavetable.getBuffer().capacity());
			} else {
			    System.out.println("\t\tunknown " + oscillator);
			}
		    }
		}

		List<SF2InstrumentRegion> instrumentRegions = instrument.getRegions();
		for (int r = 0; r < instrumentRegions.size(); r++) {
		    SF2InstrumentRegion instrumentRegion = instrumentRegions.get(r);
		    SF2Layer layer = instrumentRegion.getLayer();
		    if (layer.getGlobalRegion() != null) {
			System.out.println("\t" + layer + ", generators " + layer.getGlobalRegion().getGenerators().size());
			Map<Integer, Short> genrs = layer.getGlobalRegion().getGenerators();
		    }
		    List<SF2LayerRegion> layerRegions = layer.getRegions();
		    for (int ll = 0; ll < layerRegions.size(); ll++) {
			SF2LayerRegion layerRegion = layerRegions.get(ll);
			SF2Sample sample = layerRegion.getSample();
			System.out.println("\t\t" + sample);
		    }
		}

	    }
	    int preset = 32;
	    int sample = 4;
	    System.out.println("------------" + preset + " / " + sample);
	    for (int i = 0; i < instruments.length; i++) {
		SF2Instrument instrument = instruments[i];
		if (instrument.preset == preset) {
		    System.out.println(instrument + "`````````````````````````");
		    ModelPerformer modelPerformer = instrument.getPerformers()[sample];
		    System.out.println("\tsample: " + modelPerformer.getName() //
			    + ": range " + modelPerformer.getKeyFrom() + " / " + modelPerformer.getKeyTo());
		    List<SF2InstrumentRegion> instrumentRegions = instrument.getRegions();
		    SF2InstrumentRegion instrumentRegion = instrumentRegions.get(0);
		    SF2Layer layer = instrumentRegion.getLayer();
		    List<SF2LayerRegion> layerRegions = layer.getRegions();
		    SF2LayerRegion layerRegion = layerRegions.get(sample);
		    SF2Sample smpl = layerRegion.getSample();
		    System.out.println("\t" + smpl);

		    ModelOscillator oscillator = modelPerformer.getOscillators().get(0);
		    if (oscillator instanceof ModelByteBufferWavetable) {
			ModelByteBufferWavetable modelByteBufferWavetable = (ModelByteBufferWavetable) oscillator;
			System.out.println("\tsize: " + modelByteBufferWavetable.getBuffer().capacity());
			FileOutputStream fos = new FileOutputStream(//
				"preset" + preset//
				+ "sample" + sample //
				+ "bank" + instrument.bank//
				+ "range" + modelPerformer.getKeyFrom() + "-" + modelPerformer.getKeyTo()//
				);
			fos.write(modelByteBufferWavetable.getBuffer().array(), (int) modelByteBufferWavetable.getBuffer().arrayOffset(), (int) modelByteBufferWavetable.getBuffer().capacity());
			fos.flush();
			fos.close();
		    } else {
			System.out.println("\tunknown: " + oscillator);
		    }
		}
		//break;
	    }

	} catch (Throwable ex) {
	    ex.printStackTrace();
	}
	System.out.println("done");
    }

    public static void readChunk(InputStream stream) throws Exception {
	System.out.println("readChunk");
	String riffName = readTag(stream);
	if (riffName.toUpperCase().equals("RIFF")//
		|| riffName.toUpperCase().equals("LIST")//
		) {
	    int riffSize = readLittleEndian32(stream);
	    System.out.println(riffName + ": " + riffSize);
	    readChunk(stream);
	} else {
	    if (riffName.toUpperCase().equals("SFBK")) {
		readChunkSFBK(stream);
	    } else {
		if (riffName.toUpperCase().equals("INFO")) {
		    readChunkINFO(stream);
		} else {
		    System.out.println("data " + riffName);
		}
	    }
	}
    }

    public static void readChunkINFO(InputStream stream) throws Exception {
	System.out.println("readChunkINFO");
	String tag = readZString(stream);
	System.out.println(tag);

    }

    public static void readChunkSFBK(InputStream stream) throws Exception {

	System.out.println("readChunkSFBK");
	readChunk(stream);

    }

    public static String readTag(InputStream stream) throws Exception {
	byte[] a4 = new byte[4];
	a4[0] = (byte) stream.read();
	a4[1] = (byte) stream.read();
	a4[2] = (byte) stream.read();
	a4[3] = (byte) stream.read();
	return new String(a4);
    }

    public static String readZString(InputStream stream) throws Exception {
	int i = stream.read();
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	while (i != 0) {
	    baos.write(i);
	    i = stream.read();
	}
	return new String(baos.toByteArray());

    }

    public static int readWord(InputStream stream) throws Exception {

	int i0 = stream.read();
	int i1 = stream.read();
	return (i0 << 8) + i1;
    }

    public static int readLittleEndian32(InputStream stream) throws Exception {

	int i0 = stream.read();
	int i1 = stream.read();
	int i2 = stream.read();
	int i3 = stream.read();
	return (i3 << 24) + (i2 << 16) + (i1 << 8) + i0;
    }
}
