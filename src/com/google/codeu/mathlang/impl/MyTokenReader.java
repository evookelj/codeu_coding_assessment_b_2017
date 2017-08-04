// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.codeu.mathlang.impl;

import java.io.IOException;
import java.util.AbstractQueue;

import com.google.codeu.mathlang.core.tokens.*;
import com.google.codeu.mathlang.parsing.TokenReader;

// MY TOKEN READER
//
// This is YOUR implementation of the token reader interface. To know how
// it should work, read src/com/google/codeu/mathlang/parsing/TokenReader.java.
// You should not need to change any other files to get your token reader to
// work with the test of the system.
public final class MyTokenReader implements TokenReader {

	private String source;
	private int position;

	public MyTokenReader(String source) {
		// Your token reader will only be given a string for input. The string will
		// contain the whole source (0 or more lines).
		this.source = source;
		this.position = 0;
	}

	@Override
	public Token next() throws IOException {
		// Most of your work will take place here. For every call to |next| you should
		// return a token until you reach the end. When there are no more tokens, you
		// should return |null| to signal the end of input.

		// If for any reason you detect an error in the input, you may throw an IOException
		// which will stop all execution.

		if (this.position >= this.source.length()) {
			return null;
		}

		String part_source = source.substring(position);
		char token_start = part_source.charAt(0);

		// System.out.println("Checking semicolon");
		if (token_start==' ' || token_start=='\n') {
			this.position++;
			return this.next();
		}
		if (token_start==';') {
			this.position++;
			return new SymbolToken(';');
		}

		// check for symbol
		if (token_start=='+' 
			|| token_start=='-' 
			|| token_start=='=') {
			this.position+=1;
			return new SymbolToken(token_start);
		}

		// System.out.println("Checking comment");
		// check for comment
		if (token_start=='\"') {
			int end_comment = part_source.indexOf("\"",1);
			if (end_comment>=0) {
				this.position += end_comment + 1;
				String comment = part_source.substring(1,end_comment);
				return new StringToken(comment);
			} else {
				throw new IOException("Unclosed string");
			}
		}

		// System.out.println("Checking digit");
		// check for number
		if (isDigit(token_start)) {
			String number = Character.toString(token_start);
			int i=1;
			for (; i<part_source.length() && (isDigit(part_source.charAt(i)) || part_source.charAt(i)=='.'); i++) {
				number += part_source.charAt(i);
			}
			double num_double = Double.parseDouble(number);
			this.position += i;
			return new NumberToken(num_double);
		}

		// System.out.println("Checking variable");
		// check for variable (anything but ; here)
		String var = Character.toString(token_start);
		int i=1;
		for (; (Character.isAlphabetic(part_source.charAt(i)) || isDigit(part_source.charAt(i))); i++) {
			var += part_source.charAt(i);
		}
		this.position += var.length();
		return new NameToken(var);
	}

	public boolean isDigit(char c) {
		try {
			Integer.parseInt(Character.toString(c));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}