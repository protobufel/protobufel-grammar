//
// Copyright © 2014, David Tesler (https://github.com/protobufel)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
// * Neither the name of the <organization> nor the
// names of its contributors may be used to endorse or promote products
// derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

package com.github.protobufel.grammar;

import static com.github.protobufel.grammar.Misc.getProtocFileDescriptorProto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.protobufel.grammar.Misc.FieldTypeRefsMode;
import com.github.protobufel.grammar.ParserUtils.CommonTokenStreamEx;
import com.github.protobufel.grammar.ProtoParser.ProtoContext;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;

// TODO implement SourceInfo, redo all tests and enable
@Ignore
// @RunWith(MockitoJUnitRunner.class)
@RunWith(Parameterized.class)
public class SourceInfoProtoFileParserTest {
  private static final Logger log = LoggerFactory.getLogger(SourceInfoProtoFileParserTest.class);
  private static final List<String> TEST_PROTOS = ImmutableList.of("simple1.proto"
  // , "unittest_custom_options"
  // , "test1"
      );
  // @InjectMocks private SourceInfoProtoFileParser sourceInfoProtoFileParser;
  private ParseTreeWalker walker;
  private SourceInfoProtoFileParser protoParser;
  private ProtoContext tree;
  private ProtoParser parser;
  private FileDescriptorProto originalProto;

  @Parameters(name = "{index}:{0}")
  public static Collection<Object[]> data() {
    final ImmutableList.Builder<Object[]> builder = ImmutableList.builder();

    for (final String fileName : TEST_PROTOS) {
      builder.add(new Object[] {fileName});
    }

    return builder.build();
  }

  @Parameter
  public String protoName;

  @Before
  public void setUp() throws Exception {
    originalProto = getProtocFileDescriptorProto(protoName, true, FieldTypeRefsMode.AS_IS);
    final InputStream is = getClass().getResourceAsStream("protoc/" + protoName);
    final ProtoLexer lexer = new ProtoLexer(new ANTLRInputStream(is));
    final CommonTokenStreamEx tokens = new CommonTokenStreamEx(lexer);
    parser = new ProtoParser(tokens);
    parser.setBuildParseTree(true); // tell ANTLR to build a parse tree
    tree = parser.proto();
    protoParser = new SourceInfoProtoFileParser(tokens, protoName);
    walker = new ParseTreeWalker();
  }

  @After
  public void tearDown() throws Exception {}

  @Test
  public final void testExitField() throws Exception {
    walker.walk(protoParser, tree);
    final FileDescriptorProto proto =
        ((FileDescriptorProto.Builder) protoParser.getParsed().getProto()).build();
    // log.debug(proto.toString());
    assertThat(proto, equalTo(originalProto));

    // log.debug(tree.toStringTree(parser));
    // assert that protoc compiled GeneratedMessage.getDescriptor().getFile().toProto() equals to
    // our proto!
  }

  /*
   * @Test public final void testGetParsed() throws Exception { // TODO throw new
   * RuntimeException("not yet implemented"); }
   * 
   * @Test public final void testExitRegularImport() throws Exception { // TODO throw new
   * RuntimeException("not yet implemented"); }
   * 
   * @Test public final void testExitPublicImport() throws Exception { // TODO throw new
   * RuntimeException("not yet implemented"); }
   * 
   * @Test public final void testExitPackageStatement() throws Exception { // TODO throw new
   * RuntimeException("not yet implemented"); }
   * 
   * @Test public final void testSourceInfoProtoFileParser() throws Exception { // TODO throw new
   * RuntimeException("not yet implemented"); }
   */
}
