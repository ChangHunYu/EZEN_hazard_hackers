export default function Footer() {
  const list: string[] = ["창훈", "지은", "지혜", "태희", "예원", "채영"];
  return (
    <div className="absolute w-full py-5 text-center">
      <p className="text-gray-500">
        A project by{" "}
        <a
          className="font-semibold text-gray-600 underline-offset-4 transition-colors hover:underline"
          // href="https://twitter.com/steventey"
          target="_blank"
          rel="noopener noreferrer"
        >
          Risk Buster
        </a>
      </p>
      {list.map((p, index) => (
        <div
          key={index} // key prop 추가
          className="inline-flex cursor-pointer items-center rounded-full bg-gray-100 px-3 py-1.5 transition-colors duration-200 hover:bg-gray-200"
        >
          <p className="text-center font-medium text-gray-600">{p}</p>
        </div>
      ))}
    </div>
  );
}
