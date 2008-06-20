/*
 * Created on Sep 17, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nl.captcha.sandbox;

import nl.captcha.text.imp.DefaultTextCreator;

/**
 * @author you
 *
 * TextProducer Implementation that will return chinese characters..
 * 
 */
public class ChineseTextProducer extends DefaultTextCreator {

	private String[] simplfiedC = new String[] {
		"包括焦点",
		"新道消点",
		"服分目搜",
		"索姓名電",
		"子郵件信",
		"主旨請回",
		"電子郵件",
		"給我所有",
		"討論區明",
		"發表新文",
		"章此討論",
		"區所有文",
		"章回主題",
		"樹瀏覽搜"
		};
		
		
	public String getText(){
		return simplfiedC[generator.nextInt(simplfiedC.length) ];
	}

	 
	
}
