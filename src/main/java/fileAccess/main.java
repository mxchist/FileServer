package fileAccess;
import java.io.File;
import java.io.FileInputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.io.InputStream;
import java.util.Collections;
import java.io.RandomAccessFile;
import java.util.Scanner;

import java.io.FileNotFoundException;
import java.io.IOException;


import static java.lang.System.out;
import static java.lang.System.currentTimeMillis;

public class main {

	public static void main (String ... args) {

		exercise1();
		exercise2();
//		exercise3();
//		exercise3_slow1();
		exercise3_slow2();
	}

	private static void exercise1() {
		// exercise 1
		// Прочитать файл (около 50 байт) в байтовый массив и вывести этот массив в консоль
		File file = new File("file100b1");
		out.print(file.exists());
		FileInputStream fis;
		try {
			fis = new FileInputStream("file100b1");
			int x;
			while ((x = fis.read()) != -1) {
				out.print((char) x);
			}
		} catch (FileNotFoundException  exc) {
			exc.printStackTrace();
		} catch (IOException exc) {
			exc.printStackTrace();
		}

		System.out.println();
	}

	private static void exercise2() {
		//exercise 2
		// Последовательно сшить 5 файлов в один (файлы примерно 100 байт).
		ArrayList <InputStream> ali = new ArrayList<InputStream>();
		try {
			ali.add(new FileInputStream("file100b1"));
			ali.add(new FileInputStream("file100b2"));
			ali.add(new FileInputStream("file100b3"));
			ali.add(new FileInputStream("file100b4"));
			ali.add(new FileInputStream("file100b5"));
		}
		catch (IOException exc) {
			exc.printStackTrace();
		}

		SequenceInputStream sis = new SequenceInputStream(Collections.enumeration(ali));
		int x;

		try {
			while ((x = sis.read()) != -1) {
				out.print((char)x);
			}
			sis.close();
		}
		catch (IOException exc ) {
			exc.printStackTrace();
		}

		out.println();
	}

	private static void exercise3() {
		//exercise 3
		//Написать консольное приложение, которое умеет постранично читать текстовые файлы (размером > 10 mb).
		// Вводим страницу (за страницу можно принять 1800 символов), программа выводит ее в консоль.
		// Контролируем время выполнения: программа не должна загружаться дольше 10 секунд, а чтение – занимать свыше 5 секунд.

		long page;
		int pageSize = 1800;
		String answer = "y";
		out.println("Enter the file path:");
		String filePath = new Scanner(System.in).next();
		//d:\Max\Documents\Driver.txt
		while (answer.equals("y"))
		{
			try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
					out.println("Введите номер страницы для чтения");
					Scanner sc = new Scanner(System.in);
					page = sc.nextInt();

					int x;
					byte[] arr = new byte[pageSize];
					raf.seek((page -1) * pageSize);
					if ((raf.read(arr)) > 0) {
					out.print( new String(arr, 0, pageSize));

				};
				out.println("\nПрочитать ещё страницу? Введите y / n");
				answer = sc.next();
				if (!answer.equals("y")) {
					answer = "n";
				}
			}
			catch (FileNotFoundException exc) {
				exc.printStackTrace();
			}
			catch (IOException exc) {
				exc.printStackTrace();
			}
		}

		out.println();
	}

	private static void exercise3_slow1() {
		//exercise 3
		//Осознанно медленное приложение. Проседание производительности наблюдается со страницы 500
		long page;
		int pageSize = 1800;			// скольким символам равна одна страница
		String answer = "y";			// ответ для анализа, выводить ли страницу
		out.println("Enter the file path:");
		String filePath = new Scanner(System.in).next();	// путь файла, который будем читать
		//d:\Max\Documents\Driver.txt
		while (answer.equals("y"))		// ответ для анализа соответствует ли "да"
		{
			try (FileInputStream raf = new FileInputStream(filePath)) {
				out.println("Введите номер страницы для чтения");
				Scanner sc = new Scanner(System.in);
				page = sc.nextInt();		// считываем порядковый номер страницы

				int x; 						// переменная для записи байта из потока
				int n = 0;					// счётчик для количества считываний из потока
				long t1 = currentTimeMillis();

				while (++n <= pageSize * (page) 			// увеличиваем n и проверяем, что оно не превысило нижнюю границу страницы
						& (x = raf.read()) != -1) {			// считываем байт
					if (n < pageSize * (page-1))			// если n не дошло до верхней границы страницы, пропускаем цикл
						continue;

					out.print( (char)x);
				};
				out.printf("%nВремя чтения страницы %d составило %d миллисекунд %n", page, currentTimeMillis() - t1);
				out.println("\nПрочитать ещё страницу? Введите y / n");
				answer = sc.next();
				if (!answer.equals("y")) {
					answer = "n";
				}
			}
			catch (FileNotFoundException exc) {
				exc.printStackTrace();
			}
			catch (IOException exc) {
				exc.printStackTrace();
			}
		}

		out.println();
	}

	private static void exercise3_slow2() {
		//exercise 3
		//Попытка сделать ещ1 один вариант осознанно медленного приложения. В отличие от exercise3(),
		// чтение производится не в байтовый массив, а переменную из одного байта. В out.print() также
		// выводится по одному байту, а не формируется строка из биайтового массива. Результат - замедлить
		// не получилось, что 5 страница, что 5000 страница выводятся за одно короткое время ~ 7 мсек
		long page;
		int pageSize = 1800;
		String answer = "y";
		out.println("Enter the file path:");
		String filePath = new Scanner(System.in).next();
		//d:\Max\Documents\Driver.txt
		while (answer.equals("y"))
		{
			try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
				out.println("Введите номер страницы для чтения");
				Scanner sc = new Scanner(System.in);
				page = sc.nextInt();

				int x;
				int n = 0;					// счётчик для количества считываний из потока
				long t1 = currentTimeMillis();

				raf.seek((page -1) * pageSize);
				while ( (++n <= pageSize ) & ( x = raf.read()) != -1) {
					out.print( (char)x );

				};

				out.printf("%nВремя чтения страницы %d составило %d миллисекунд %n", page, currentTimeMillis() - t1);
				out.println("\nПрочитать ещё страницу? Введите y / n");
				answer = sc.next();
				if (!answer.equals("y")) {
					answer = "n";
				}
			}
			catch (FileNotFoundException exc) {
				exc.printStackTrace();
			}
			catch (IOException exc) {
				exc.printStackTrace();
			}
		}

		out.println();
	}

}
